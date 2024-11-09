package com.muscleup.muscleup.ui.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;

import com.muscleup.muscleup.R;

import java.util.ArrayList;
import java.util.Collections;

public class DevicesFragment extends ListFragment {

    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<BluetoothDevice> listItems = new ArrayList<>();
    private ArrayAdapter<BluetoothDevice> listAdapter;
    ActivityResultLauncher<String> requestBluetoothPermissionLauncherForRefresh;
    private Menu menu;
    private boolean permissionMissing;
    @SuppressLint("StaticFieldLeak")
    public static TerminalFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(requireActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listAdapter = new ArrayAdapter<BluetoothDevice>(requireActivity(), 0, listItems) {
            @NonNull
            @Override
            public View getView(int position, View view, @NonNull ViewGroup parent) {
                BluetoothDevice device = listItems.get(position);
                if (view == null)
                    view = requireActivity().getLayoutInflater().inflate(R.layout.device_list_item, parent, false);
                TextView text1 = view.findViewById(R.id.text1);
                TextView text2 = view.findViewById(R.id.text2);
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                text1.setText(deviceName);
                text2.setText(device.getAddress());
                return view;
            }
        };
        requestBluetoothPermissionLauncherForRefresh = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> BluetoothUtil.onPermissionsResult(this, granted, this::refresh));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(null);
        View header = requireActivity().getLayoutInflater().inflate(R.layout.device_list_header, null, false);
        getListView().addHeaderView(header, null, false);
        setEmptyText("initializing...");
        ((TextView) getListView().getEmptyView()).setTextSize(18);
        setListAdapter(listAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.menu_devices, menu);
        if(permissionMissing)
            menu.findItem(R.id.bt_refresh).setVisible(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bt_refresh) {
            if(BluetoothUtil.hasPermissions(this, requestBluetoothPermissionLauncherForRefresh))
                refresh();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("MissingPermission")
    void refresh() {
        listItems.clear();
        if(bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionMissing = requireActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED;
                if(menu != null && menu.findItem(R.id.bt_refresh) != null)
                    menu.findItem(R.id.bt_refresh).setVisible(permissionMissing);
            }
            if(!permissionMissing) {
                for (BluetoothDevice device : bluetoothAdapter.getBondedDevices())
                    if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE)
                        listItems.add(device);
                listItems.sort(BluetoothUtil::compareTo);
            }
        }
        if(bluetoothAdapter == null)
            setEmptyText(getString(R.string.bluetooth_not_supported));
        else if(!bluetoothAdapter.isEnabled())
            setEmptyText(getString(R.string.bluetooth_is_disabled));
        else if(permissionMissing)
            setEmptyText(getString(R.string.permission_missing_use_refresh));
        else
            setEmptyText(getString(R.string.no_bluetooth_devices_found));
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        BluetoothDevice device = listItems.get(position-1);
        Bundle args = new Bundle();
        args.putString("device", device.getAddress());
        fragment = new TerminalFragment();
        fragment.setArguments(args);
        getParentFragmentManager().beginTransaction().replace(R.id.fragment, fragment, "terminal").addToBackStack(null).commit();
    }
}
