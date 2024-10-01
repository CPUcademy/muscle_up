package com.muscleup.muscleup.ui.settings;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.muscleup.muscleup.Functions;
import com.muscleup.muscleup.R;
import com.muscleup.muscleup.ui.home.SessionModel;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class TerminalFragment extends Fragment implements ServiceConnection, SerialListener
{
    private enum Connected { False, Pending, True }
    private String deviceAddress;
    private SerialService service;
    private TextView receiveText;
    public static boolean ifConnected = false;
    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private final boolean hexEnabled = false;
    private boolean pendingNewline = false;
    private final String newline = TextUtil.newline_crlf;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        deviceAddress = getArguments().getString("device");
    }

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            requireActivity().startService(new Intent(requireActivity(), SerialService.class));
    }

    @Override
    public void onStop() {
        if(service != null && !requireActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        requireActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try { requireActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(initialStart && service != null) {
            initialStart = false;
            requireActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart && isResumed()) {
            initialStart = false;
            requireActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        receiveText = view.findViewById(R.id.receive_text);
        receiveText.setTextColor(getResources().getColor(R.color.white));
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        return view;
    }

    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status(getString(R.string.connectingg));
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(requireActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
        ifConnected = false;
    }

    private void send(String str) {
        if(connected != Connected.True) {
            connect();
        }
        try {
            String msg;
            byte[] data;
            if(hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();
            }
            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            spn.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.white)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(ArrayDeque<byte[]> datas) {
        SpannableStringBuilder spn = new SpannableStringBuilder();
        for (byte[] data : datas) {
            if (hexEnabled) {
                spn.append(TextUtil.toHexString(data)).append('\n');
            } else {
                String msg = new String(data);
                if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                    msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
                    if (pendingNewline && msg.charAt(0) == '\n') {
                        if (spn.length() >= 2) {
                            spn.delete(spn.length() - 2, spn.length());
                        } else {
                            Editable edt = receiveText.getEditableText();
                            if (edt != null && edt.length() >= 2)
                                edt.delete(edt.length() - 2, edt.length());
                        }
                    }
                    pendingNewline = msg.charAt(msg.length() - 1) == '\r';
                }
                spn.append(TextUtil.toCaretString(msg, newline.length() != 0));
            }
        }

        if (spn.charAt(0) == 'A') {
            spn = SpannableStringBuilder.valueOf(spn.toString().substring(1));
            String[] parts = spn.toString().split("\\|");

            int form = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            int repsDone = Integer.parseInt(parts[3]);
            String statsFilledString = parts[4];

            ArrayList<SessionModel> sessionList = new ArrayList<>();
            statsFilledString = statsFilledString.replaceAll("[{}']", "");
            String[] exercises = statsFilledString.split(", ");

            for (String exercise : exercises) {
                String[] keyValue = exercise.split(": ");
                String exerciseName = keyValue[0];
                int reps = Integer.parseInt(keyValue[1]);
                sessionList.add(new SessionModel(exerciseName, reps));
            }
            Functions.afterSessionStats(form, seconds, repsDone, sessionList, requireContext());
        }
    }

    public void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    public void sending(String s)
    {
        send(s);
    }

    @Override
    public void onSerialConnect() {
        status(getString(R.string.connectedd));
        connected = Connected.True;
        ifConnected = true;
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status(getString(R.string.connection_failed) + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        ArrayDeque<byte[]> datas = new ArrayDeque<>();
        datas.add(data);
        receive(datas);
    }

    public void onSerialRead(ArrayDeque<byte[]> datas) {
        receive(datas);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status(getString(R.string.connection_lost) + e.getMessage());
        disconnect();
    }

}
