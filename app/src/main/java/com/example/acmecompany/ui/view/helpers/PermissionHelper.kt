package com.example.acmecompany.ui.view.helpers

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.acmecompany.App


class PermissionHelper
{
    companion object
    {

        /**
         * Check if the given permission is granted. It always returns true in android versions under the API 23.
         *
         * @param permission One of the permissions in the [android.Manifest.permission] class.
         * @return 'true' if the permission is granted.
         */
        fun isGranted(permission: String): Boolean
        {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                true
            else
                ContextCompat.checkSelfPermission(
                    App.context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * Check if the given permission is denied. It always returns false in android versions under the API 23.
         *
         * @param permission One of the permissions in the [android.Manifest.permission] class.
         * @return 'true' if the permission is denied.
         */
        fun isDenied(permission: String): Boolean
        {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                false
            else
                ContextCompat.checkSelfPermission(
                    App.context,
                    permission
                ) == PackageManager.PERMISSION_DENIED
        }

        /**
         * Returns true if all the given permissions are granted. If one not granted we request the
         * permissions using the requestCode.
         *
         * The activity can override the [Activity.onRequestPermissionsResult]
         * method as a callback and check if permission was granted or denied.
         *
         * @param activity    The activity.
         * @param permissions Array of the permissions in the [android.Manifest.permission] class.
         * @param requestCode The request code passed to the [Activity.onRequestPermissionsResult]
         */
        fun checkForPermission(
            activity: Activity,
            permissions: Array<String>,
            requestCode: Int
        ): Boolean
        {
            for (permission in permissions)
            {
                if (isDenied(permission))
                {
                    ActivityCompat.requestPermissions(activity, permissions, requestCode)
                    return false
                }
            }

            return true
        }
    }
}