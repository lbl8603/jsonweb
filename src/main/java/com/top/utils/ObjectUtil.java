package com.top.utils;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

/**
 * @author lubeilin
 * @date 2021/1/13
 */
public class ObjectUtil {
    public static Object convert(Class<?> targetType, String s) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(s);
        return editor.getValue();
    }
}
