package com.journeytech.mark.mark.getaccuratelocation.permission;

import java.io.Serializable;

public interface PermissionManagerInterface extends Serializable
{
    String TAG = PermissionManagerInterface.class.getSimpleName();

    void onPermissionGranted(String message, int requestCode);

    void onPermissionDenied(String message, int requestCode);

    void isAllGranted(boolean flag);
}
