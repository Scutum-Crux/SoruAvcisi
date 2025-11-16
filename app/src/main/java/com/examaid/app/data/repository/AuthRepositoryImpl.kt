package com.examaid.app.data.repository

import android.util.Log
import com.examaid.app.core.util.Resource
import com.examaid.app.domain.model.User
import com.examaid.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    
    private val logTag = "AuthRepositoryImpl"
    
    override suspend fun loginWithEmail(email: String, password: String): Resource<User> {
        return try {
            Log.d(logTag, "loginWithEmail(email=$email)")
            println("$logTag: loginWithEmail(email=$email)")
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.Error("Kullanıcı bulunamadı")

            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString()
            )

            if (!firebaseUser.isEmailVerified) {
                firebaseAuth.signOut()
                Log.w(logTag, "loginWithEmail blocked - email not verified for ${user.id}")
                println("$logTag: email not verified for ${user.id}")
                return Resource.Error("Giriş yapmadan önce e-posta adresini doğrulamalısın.")
            }
            
            // Update last login time in Firestore (non-blocking)
            Log.d(logTag, "loginWithEmail success for ${user.id}")
            println("$logTag: loginWithEmail success for ${user.id}")
            
            Resource.Success(user)
        } catch (e: Exception) {
            Log.e(logTag, "loginWithEmail failed", e)
            println("$logTag: loginWithEmail failed -> ${e.localizedMessage}")
            Resource.Error(e.message ?: "Giriş başarısız")
        }
    }
    
    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Resource<User> {
        return try {
            Log.d(logTag, "registerWithEmail(email=$email, displayName=$displayName)")
            println("$logTag: registerWithEmail(email=$email, displayName=$displayName)")
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.Error("Kullanıcı oluşturulamadı")
            
            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            try {
                firebaseUser.sendEmailVerification().await()
            } catch (e: Exception) {
                Log.w(logTag, "sendEmailVerification failed", e)
                println("$logTag: sendEmailVerification failed -> ${e.localizedMessage}")
                e.printStackTrace()
            }
            
            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = displayName,
                photoUrl = null
            )
            
            Log.d(logTag, "registerWithEmail success for ${user.id}")
            println("$logTag: registerWithEmail success for ${user.id}")
            
            Resource.Success(user)
        } catch (e: Exception) {
            Log.e(logTag, "registerWithEmail failed", e)
            println("$logTag: registerWithEmail failed -> ${e.localizedMessage}")
            Resource.Error(e.message ?: "Kayıt başarısız")
        }
    }
    
    override suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: return Resource.Error("Google girişi başarısız")
            
            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString()
            )
            
            Log.d(logTag, "loginWithGoogle success for ${user.id}")
            println("$logTag: loginWithGoogle success for ${user.id}")
            
            Resource.Success(user)
        } catch (e: Exception) {
            Log.e(logTag, "loginWithGoogle failed", e)
            println("$logTag: loginWithGoogle failed -> ${e.localizedMessage}")
            Resource.Error(e.message ?: "Google girişi başarısız")
        }
    }
    
    override suspend fun logout() {
        firebaseAuth.signOut()
    }
    
    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        
        return try {
            // Try to get user from Firestore
            val document = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()
            
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                // Fallback to Firebase Auth user
                User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoUrl?.toString()
                )
            }
        } catch (e: Exception) {
            // Fallback to Firebase Auth user
            User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString()
            )
        }
    }
    
    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
    
    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return try {
            Log.d(logTag, "sendPasswordResetEmail(email=$email)")
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(logTag, "sendPasswordResetEmail failed", e)
            Resource.Error(e.message ?: "Şifre sıfırlama e-postası gönderilemedi")
        }
    }
    
    override fun observeAuthState(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            val user = firebaseUser?.let {
                User(
                    id = it.uid,
                    email = it.email ?: "",
                    displayName = it.displayName,
                    photoUrl = it.photoUrl?.toString()
                )
            }
            trySend(user)
        }
        
        firebaseAuth.addAuthStateListener(listener)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }
    
    private suspend fun saveUserToFirestore(user: User) {
        try {
            Log.d(logTag, "saveUserToFirestore start uid=${user.id}")
            println("$logTag: saveUserToFirestore start uid=${user.id}")
            firestore.collection("users")
                .document(user.id)
                .set(user)
                .addOnSuccessListener {
                    Log.d(logTag, "saveUserToFirestore success uid=${user.id}")
                    println("$logTag: saveUserToFirestore success uid=${user.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(logTag, "saveUserToFirestore failed", e)
                    println("$logTag: saveUserToFirestore failed -> ${e.localizedMessage}")
                }
                .await()
        } catch (e: Exception) {
            // Log error but don't throw - user is still authenticated
            Log.e(logTag, "saveUserToFirestore failed", e)
            println("$logTag: saveUserToFirestore failed -> ${e.localizedMessage}")
            e.printStackTrace()
        }
    }
    
    private suspend fun updateUserInFirestore(user: User) {
        try {
            Log.d(logTag, "updateUserInFirestore start uid=${user.id}")
            println("$logTag: updateUserInFirestore start uid=${user.id}")
            firestore.collection("users")
                .document(user.id)
                .set(user)
                .addOnSuccessListener {
                    Log.d(logTag, "updateUserInFirestore success uid=${user.id}")
                    println("$logTag: updateUserInFirestore success uid=${user.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(logTag, "updateUserInFirestore failed", e)
                    println("$logTag: updateUserInFirestore failed -> ${e.localizedMessage}")
                }
                .await()
        } catch (e: Exception) {
            Log.e(logTag, "updateUserInFirestore failed", e)
            println("$logTag: updateUserInFirestore failed -> ${e.localizedMessage}")
            e.printStackTrace()
        }
    }
}

