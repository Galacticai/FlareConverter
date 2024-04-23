package global.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity


fun restartApp(context: AppCompatActivity, startupActivity: Class<out AppCompatActivity>) {
    context.startActivity(Intent(context, startupActivity))
    context.finishAffinity()
}