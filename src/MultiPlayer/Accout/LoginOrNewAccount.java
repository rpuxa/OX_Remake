package MultiPlayer.Accout;

public class LoginOrNewAccount {
    private Object data = null;
    private int operation;

    public static final int LOGIN = 1;
    public static final int NEW_ACCOUNT = 2;
    public static final int CANCELED = 0;
    static boolean isCanceled = false;

    public LoginOrNewAccount(int message){
        Login.pass_plus_login = null;
        NewAccount.profile = null;
        isCanceled = false;
        Thread login = new Thread(() -> new Login(message));
        login.start();
        Thread newAccount = new Thread(NewAccount::new);
        newAccount.start();
        try {
            login.join();
            newAccount.join();
        } catch (InterruptedException ignore){
        }
        if (isCanceled){
            operation = CANCELED;
        } else {
            if (NewAccount.profile != null){
                data = NewAccount.pass_plus_login;
                operation = NEW_ACCOUNT;
            } else {
                data = Login.pass_plus_login;
                operation = LOGIN;
            }
        }
    }

    public int getOperation() {
        return operation;
    }

    public Object getData() {
        return data;
    }
}
