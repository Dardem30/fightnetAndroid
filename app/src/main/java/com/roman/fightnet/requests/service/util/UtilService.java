package com.roman.fightnet.requests.service.util;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.roman.fightnet.requests.service.AuthService;
import com.roman.fightnet.requests.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilService {
    private static final AuthService authService = new AuthService();
    private static final UserService userService = new UserService();

    public static AuthService getAuthService() {
        return authService;
    }

    public static UserService getUserService() {
        return userService;
    }

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
}
