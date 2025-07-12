package cl.libroypunto.libroypunto

import android.app.Application
import com.google.firebase.FirebaseApp

class LibroYPuntoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 