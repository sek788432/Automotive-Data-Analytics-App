package e.user.client_app;

import android.support.graphics.drawable.PathInterpolatorCompat;
import android.support.v7.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SocketHandler extends AppCompatActivity {
    /* access modifiers changed from: private */
    public static Socket socket = null;
    public String[][] Data = new String[4][];
    public double[][] Data_double = ((double[][]) Array.newInstance(double.class, new int[]{4, 100}));
    public String[] Personal_data;
    String message = " ";
    String number;
    byte[] picture;
    public int req;
    SocketAddress sc_add = null;
    InetAddress serverAddr = null;
    int varity;

    class myThread extends Thread {
        myThread() {
        }

        public void run() {
            try {
                SocketHandler.this.serverAddr = InetAddress.getByName("192.168.43.67");
                SocketHandler.this.sc_add = new InetSocketAddress(SocketHandler.this.serverAddr, 6666);
                Socket unused = SocketHandler.socket = new Socket();
                SocketHandler.socket.connect(SocketHandler.this.sc_add, PathInterpolatorCompat.MAX_NUM_POINTS);
                if (SocketHandler.this.req == 2) {
                    History();
                } else if (SocketHandler.this.req == 10) {
                    Setting();
                } else if (SocketHandler.this.req == 3) {
                    Check();
                }
            } catch (UnknownHostException e2) {
                System.out.println("1bug");
            } catch (SocketException e3) {
                e3.getMessage();
                System.out.println(e3);
            } catch (IOException e4) {
                e4.getMessage();
                System.out.println(e4);
            }
        }

        public void History() throws IOException {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(SocketHandler.socket.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(SocketHandler.socket.getInputStream()));
            bw.write(2);
            bw.flush();
            br.readLine();
            bw.write(SocketHandler.this.number);
            bw.flush();
            String[] data = {br.readLine(), br.readLine(), br.readLine(), br.readLine()};
            for (int j = 0; j < 4; j++) {
                SocketHandler.this.Data[j] = data[j].split(",");
                for (int i = 0; i < 100; i++) {
                    SocketHandler.this.Data_double[j][i] = Double.parseDouble(SocketHandler.this.Data[j][i]);
                }
            }
        }

        public void Setting() {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(SocketHandler.socket.getOutputStream()));
                BufferedReader br = new BufferedReader(new InputStreamReader(SocketHandler.socket.getInputStream()));
                bw.write(10);
                bw.flush();
                br.readLine();
                for (int i = 0; i < 8; i++) {
                    bw.write(SocketHandler.this.Personal_data[i]);
                    bw.flush();
                    br.readLine();
                }
                DataOutputStream dOut = new DataOutputStream(SocketHandler.socket.getOutputStream());
                System.out.println("size " + SocketHandler.this.picture.length);
                bw.write(String.valueOf(SocketHandler.this.picture.length));
                bw.flush();
                br.readLine();
                for (int i2 = 0; i2 < SocketHandler.this.picture.length; i2++) {
                    dOut.write(SocketHandler.this.picture);
                }
                dOut.flush();
                br.readLine();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        public void Check() {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(SocketHandler.socket.getOutputStream()));
                BufferedReader br = new BufferedReader(new InputStreamReader(SocketHandler.socket.getInputStream()));
                bw.write(3);
                bw.flush();
                br.readLine();
                bw.write(SocketHandler.this.number);
                bw.flush();
                String errorCode = br.readLine();
                System.out.println(errorCode);
                if (errorCode.charAt(0) == '1') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("引擎溫度太高\n");
                } else if (errorCode.charAt(0) == '2') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("引擎溫度太低\n");
                }
                if (errorCode.charAt(1) == '1') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("氣流太高\n");
                } else if (errorCode.charAt(1) == '2') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("氣流太低\n");
                }
                if (errorCode.charAt(2) == '1') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("Load太高\n");
                } else if (errorCode.charAt(2) == '2') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("Load太低\n");
                }
                if (errorCode.charAt(3) == '1') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("油耗太高\n");
                } else if (errorCode.charAt(3) == '2') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("油耗太低\n");
                }
                if (errorCode.charAt(4) == '1') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("電壓太高\n");
                } else if (errorCode.charAt(4) == '2') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("電壓太低\n");
                }
                if (errorCode.charAt(5) == '1') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("引擎轉速太高\n");
                } else if (errorCode.charAt(5) == '2') {
                    SocketHandler.this.message = SocketHandler.this.message.concat("引擎轉速太低\n");
                }
                PrintStream printStream = System.out;
                printStream.println("error Code here equals :" + errorCode);
                if (errorCode.equals("000000")) {
                    SocketHandler.this.message = "There's no problem\n";
                    SocketHandler.this.varity = 1;
                } else {
                    SocketHandler.this.varity = 0;
                }
                PrintStream printStream2 = System.out;
                printStream2.println("In socketHandler: " + SocketHandler.this.message);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void con() {
        Thread t1 = new myThread();
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }

    public void setReq(int request) {
        this.req = request;
    }

    public void setPersonal_data(String[] a) {
        this.Personal_data = a;
    }

    public double[][] getData() {
        return this.Data_double;
    }

    public void setPicture(byte[] uri) {
        this.picture = uri;
    }

    public String getMess() {
        return this.message;
    }

    public void setNumber(String a) {
        this.number = a;
    }

    public int get_variety() {
        return this.varity;
    }
}
