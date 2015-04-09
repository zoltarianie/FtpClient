package com.zoltarianie.ftpclient.src;

public class pCaller {
    private static pCaller ourInstance = new pCaller();

    public static pCaller getInstance() {
        return ourInstance;
    }

    private pCaller() {

    }

    private pCommand oPCommand;
    public void command_execute(pCommand oPCommand){
        this.oPCommand = oPCommand;
        this.oPCommand.execute();
    }

    public void command_exit(pCommand oPCommand){
        this.oPCommand = oPCommand;
        this.oPCommand.exit();
    }

    public void command_exit(){
        this.oPCommand.exit();
    }

}
