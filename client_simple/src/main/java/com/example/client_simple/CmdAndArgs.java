package com.example.client_simple;

import java.io.Serializable;

public class CmdAndArgs implements Serializable {
    private String cmd;
    private String arg;


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    @Override
    public String toString() {
        return cmd + " " + arg;
    }
}
