package com.roman.fightnet.requests.service.util;


import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.roman.fightnet.requests.service.AuthService;
import com.roman.fightnet.requests.service.SocketService;
import com.roman.fightnet.requests.service.UserService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilService {
    private static final AuthService authService = new AuthService();
    private static final UserService userService = new UserService();
    private static final SocketService socketService = new SocketService();
    private static final Gson gson = new Gson();

    public static AuthService getAuthService() {
        return authService;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static SocketService getSocketService() {
        return socketService;
    }

    @Deprecated
    public static String createJson(final Object parent, final  Object... fieldObjects) {
        final Map<String, String> names = new HashMap<>(fieldObjects.length);
        final StringBuilder builder = new StringBuilder();
        String finalName = null;
        for (java.lang.reflect.Field field : parent.getClass().getFields()) {
            final Object currentFieldObject;
            try {
                currentFieldObject = field.get(parent);
            } catch (final Exception e) {
                continue;
            }
            for (final Object fieldObject: fieldObjects) {
                if (fieldObject.equals(currentFieldObject)) {
                    if (fieldObject instanceof EditText) {
                        names.put(field.getName(), ((EditText) fieldObject).getText().toString());
                    } else {
                        names.put(field.getName(), fieldObject.toString());
                    }
                    finalName = field.getName();
                    break;
                }
            }
        }
        builder.append("{");
        for (final Map.Entry<String, String> entry: names.entrySet()) {
            builder.append("'").append(entry.getKey()).append("'").append(":").append("'").append(entry.getValue()).append("'");
            if (!entry.getKey().equals(finalName)) {
                builder.append(",");
            }
        }
        builder.append("}");
        return builder.toString();
    }

    public static ArrayAdapter<String> setupStringAdapter(final List<String> items, final Context context) {
        final ArrayAdapter<String> adapter =  new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, items){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public static Object castJsonToObject(final String json, final Class clazz) {
        return gson.fromJson(json, clazz);
    }

    public static void setupDatepickersDate(final Context context, final EditText... fields) {
        for (final EditText field: fields) {
            field.setInputType(InputType.TYPE_NULL);
            field.setOnClickListener(v -> {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                new DatePickerDialog(context,
                        (view, year1, monthOfYear, dayOfMonth) -> field.setText(year1 + "-" + (monthOfYear + 1 > 9 ? monthOfYear + 1 : "0" + (monthOfYear + 1)) + "-" + (dayOfMonth > 9 ? dayOfMonth : "0" + dayOfMonth)), year, month, day).show();
            });
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
