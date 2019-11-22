package aa.bb.serialdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SerialPortManager _serialPortManager;
    Device            _device;

    TextView           _infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openPortButton = findViewById(R.id.open_serail_port_button);
        Button writeDataButton = findViewById(R.id.write_data_button);
        Button closePortButton = findViewById(R.id.close_port_button);
        _infoTextView = findViewById(R.id.info);
        _infoTextView.setText("");

        _serialPortManager = new SerialPortManager();

        SerialPortFinder serialPortFinder = new SerialPortFinder();
        ArrayList<Device> devices = serialPortFinder.getDevices();
        for ( int i = 0; i< devices.size(); i++ ){
            Device device = devices.get(i);
            if ( device.getName().equalsIgnoreCase(DeviceConst.SERIAL_PORT_NAME) ){
                _device = device;
            }
        }

        _serialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File device) {
                String info = _device.getName() + "串口打开成功";
                _infoTextView.setText(info);
            }

            @Override
            public void onFail(File device, Status status) {
                String info = _device.getName() + "串口打开失败";
                _infoTextView.setText(info);
            }
        });

        _serialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                String dataString = BytesHexStrTranslate.bytesToHexFun1(bytes);
                final String info = "收到数据，16进制数据为 " + dataString;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _infoTextView.setText(info);
                    }
                });
            }

            @Override
            public void onDataSent(byte[] bytes) {
                String dataString = BytesHexStrTranslate.bytesToHexFun1(bytes);
                final String info = "16进制数据 " + dataString + " 发送成功";

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _infoTextView.setText(info);
                    }
                });
            }
        });

        openPortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( null != _device ){
                    _serialPortManager.openSerialPort(_device.getFile(), 115200);
                }
            }
        });

        writeDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sendBytes = _serialPortManager.sendBytes("1".getBytes());
                if ( !sendBytes ) _infoTextView.setText("数据发送失败");
            }
        });

        closePortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _serialPortManager.closeSerialPort();
                _device = null;
                _infoTextView.setText("串口已关闭");
            }
        });
    }
}
