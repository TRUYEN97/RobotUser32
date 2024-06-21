/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qt.user32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class User32Util {

    private final User32 user32 = User32.INSTANCE;

    public List<Integer> findProcess(String targetExeName) {
        WinDef.HWND hwnd = user32.GetDesktopWindow();
        WinDef.HWND hwndChild = user32.GetWindow(hwnd, User32.GW_CHILD);
        List<Integer> ids = new ArrayList<>();
        while (hwndChild != null) {
            char[] windowText = new char[512];
            user32.GetWindowText(hwndChild, windowText, 512);
            String windowTitle = Native.toString(windowText);
            if (windowTitle.equalsIgnoreCase(targetExeName)) {
                ids.add(getProcessId(hwndChild));
            }
            hwndChild = user32.GetWindow(hwndChild, User32.GW_HWNDNEXT);
        }
        return ids;
    }

    public boolean showWindows(String windowName) {
        WinDef.HWND hwnd = user32.FindWindow(0, windowName);
        return showWindows(hwnd);
    }

    public boolean showWindows(WinDef.HWND hwnd) {
        if (hwnd == null) {
            return false;
        }
        return user32.ShowWindow(hwnd, 1) && user32.SetForegroundWindow(hwnd);
    }

    public boolean killProcessByName(String processName) {
        if(processName == null){
            return false;
        }
        return user32.EnumWindows((WinDef.HWND hwnd, Pointer data) -> {
            char[] windowText = new char[512];
            user32.GetWindowText(hwnd, windowText, 512);
            String windowTitle = Native.toString(windowText);
            if (windowTitle.equalsIgnoreCase(processName)) {
                return killProcessByPID(getProcessId(hwnd));
            }
            return true;
        }, null);
    }
    
    public boolean killProcessByPID(int processId){
        return Kernel32.INSTANCE.TerminateProcess(
                        Kernel32.INSTANCE.OpenProcess(
                                WinNT.PROCESS_TERMINATE, false, processId), 0);
    }

    public int getProcessId(WinDef.HWND hwnd) {
        IntByReference pid = new IntByReference();
        user32.GetWindowThreadProcessId(hwnd, pid);
        return pid.getValue();
    }

    public String getWindowTitle(WinDef.HWND hwnd) {
        char[] title = new char[512];
        user32.GetWindowText(hwnd, title, title.length);
        return Native.toString(title);
    }

    
}
