package sabal.bluetooth_tts;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    String all = "";
    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    private TextToSpeech mTTS;
    ArrayList<String> Adresses = new ArrayList<String>();
    ArrayList<String> Texts = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mTTS = new TextToSpeech(this, this);
        if (bluetooth != null && !bluetooth.isEnabled()) {
            // С Bluetooth все в порядке.
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
        String status;
        if (bluetooth.isEnabled()) {
            String mydeviceaddress = bluetooth.getAddress();
            String mydevicename = bluetooth.getName();
            status = mydevicename + " : " + mydeviceaddress;
        } else {
            status = "Bluetooth выключен";
        }
        /*StringBuilder text = new StringBuilder();
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard,"adress.txt");
            FileInputStream fstream1 = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in,"windows-1251"));
            String line = "No";
            mTTS.speak(line, TextToSpeech.QUEUE_FLUSH, null);
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                Adresses.add(line);
                line = br.readLine();
                Texts.add(line);
            }
            br.close() ;
        }catch (IOException e) {
            e.printStackTrace();
        }*/

        //Toast.makeText(this, status, Toast.LENGTH_LONG).show();
        bluetooth.startDiscovery();
        // Регистрируем BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);// Не забудьте снять регистрацию в onDestroy


    }

    // Создаем BroadcastReceiver для ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Toast.makeText(MainActivity.this, "Скан", Toast.LENGTH_LONG).show();
            // Когда найдено новое устройство
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Получаем объект BluetoothDevice из интента
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Добавляем имя и адрес в array adapter, чтобы показвать в ListView
                if (device != null) {
                    if ((device.getName() != null) && (device.getName().length() > 0)) {
                        all += device.getName() + " : " + device.getAddress() + " : " + device.getType() + " : " + new Date(System.currentTimeMillis()).getHours()+ ":" + new Date(System.currentTimeMillis()).getMinutes() + "\n";
                        //Toast.makeText(MainActivity.this, "Сосканировало", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, device.getName() + "     " + device.getAddress(), Toast.LENGTH_LONG).show();
                        TextView tw = (TextView) findViewById(R.id.textview);
                        tw.setText(all);
                        int c = isInWL(device.getAddress());
                        if (c != -1) {
                            mTTS.speak(Texts.get(c), TextToSpeech.QUEUE_FLUSH, null);
                        }
                        if (device.getName().equals("sabal pad")) {
                            mTTS.speak("Планшет Сабала", TextToSpeech.QUEUE_FLUSH, null);
                        }
                        if (device.getName().equals("HC-06")) {
                            mTTS.speak("Маяк 1", TextToSpeech.QUEUE_FLUSH, null);
                        }

                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                bluetooth.startDiscovery();
            }
        }
    };

    @Override
    public void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("ru", "en");

            int result = mTTS.setLanguage(locale);
            //int result = mTTS.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Извините, этот язык не поддерживается");
            } else {
                // mButton.setEnabled(true);
            }

        } else {
            Log.e("TTS", "Ошибка!");
        }

    }
    int isInWL (String a) {
        for (int i = 0; i < Adresses.size(); i++) {
            Toast.makeText(MainActivity.this, Texts.get(i), Toast.LENGTH_SHORT).show();
            mTTS.speak("Ррптам", TextToSpeech.QUEUE_FLUSH, null);
            if (a.equals(Adresses.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
