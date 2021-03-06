package com.ltdd.steptrackerandroidapp;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ServiceLoader;

public class FR_TrangChu extends Fragment implements SensorEventListener {
    TextView tv_thoigian, tv_sobuoc, tv_stepdetector, tv_quangduong, tv_kcal;
    Button btn_hoantac, btn_luu;
    SensorManager sensorManager;
    boolean running = false;
    BottomNavigationView bottomNavigationView;
    Sensor mStepCounter, mStepDetector;
    boolean isCounterSensorPresent, isDetectorSensorPresent;
    int demBuoc = 0, stepDetect = 0;
    String tenDangNhap = MainActivity.TENDANGNHAP;
    DBHelper DB;
    int idThongKe = 0;
    boolean flag = true;
    View view;
    boolean mode=false;
    public void setMode(boolean darkMode){
        this.mode = darkMode;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_f_r__trang_chu, container, false);
        AnhXa();
        ThietLapThoiGian();

        //      đổi màu
        setMode(mode);
        if (mode==true) {
            view.findViewById(R.id.img_tieude).setBackgroundResource(R.drawable.trang_chu_background_dark);
            ((ImageView)this.view.findViewById(R.id.img_circle_yellow)).setImageResource(R.drawable.circle_lightblue);
        }
        else{
            view.findViewById(R.id.img_tieude).setBackgroundResource(R.drawable.trang_chu_background_light);
            ((ImageView)this.view.findViewById(R.id.img_circle_yellow)).setImageResource(R.drawable.circle_yellow);
        }
//      end doi mau


        // Lay ngay hien tai
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String ngay = sdf.format(calendar.getTime());

        // Chuyen doi
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date date = null;
        java.sql.Date sqlThoiGian = null;
        try {
            date = sdf1.parse(ngay);
            sqlThoiGian = new java.sql.Date(date.getTime());
        } catch (ParseException e) {
            Toast.makeText(getActivity(), "Ngày không hợp lệ, vui lòng kiểm tra lại (dd-MM-yyyy).", Toast.LENGTH_SHORT).show();
            flag = false;
        }

//        if (DB.getNgay().compareTo(sqlThoiGian) > 0) {
//            demBuoc = 0;
//            tv_sobuoc.setText(String.valueOf(demBuoc));
//        }



        btn_luu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cac buoc them ThongKe
                // Lay ngay hien tai
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String ngay = sdf.format(calendar.getTime());

                // Chuyen doi
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                java.util.Date date = null;
                java.sql.Date sqlThoiGian = null;
                try {
                    date = sdf1.parse(ngay);
                    sqlThoiGian = new java.sql.Date(date.getTime());
                } catch (ParseException e) {
                    Toast.makeText(getActivity(), "Ngày không hợp lệ, vui lòng kiểm tra lại (dd-MM-yyyy).", Toast.LENGTH_SHORT).show();
                    flag = false;
                }

                Boolean themThongKe = DB.themThongKe(demBuoc, (demBuoc * 50) / 100, sqlThoiGian);
                idThongKe = DB.getIDThongKe();
//                if (themThongKe) {
//                    Toast.makeText(getActivity().getApplication(), "Thêm ThongKe thành công!", Toast.LENGTH_SHORT).show();
//                    idThongKe = DB.getIDThongKe();
//
//                } else {
//                    Toast.makeText(getActivity().getApplication(), "Thử lại!", Toast.LENGTH_SHORT).show();
//                }

                // Them ChiTietThongKe
                Boolean themChiTietThongKe = DB.themChiTietThongKe(idThongKe, tenDangNhap);
//                if (themChiTietThongKe) {
//                    Toast.makeText(getActivity().getApplication(), "Thêm CTTK thành công!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getActivity().getApplication(), "Thử lại!", Toast.LENGTH_SHORT).show();
//                }
//
                Toast.makeText(getActivity().getApplication(), "Lưu thành công!", Toast.LENGTH_SHORT).show();
            }

        });

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        }
        else {
            Toast.makeText(getActivity(), "Bộ đếm không hoạt động!", Toast.LENGTH_SHORT).show();
            isCounterSensorPresent = false;
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isDetectorSensorPresent = true;
        } else {
            Toast.makeText(getActivity(), "Cảm biến không hoạt động!", Toast.LENGTH_SHORT).show();
            isDetectorSensorPresent = false;
        }

        return view;
    }

    private void AnhXa() {
        tv_thoigian = (TextView) view.findViewById(R.id.tv_thoigian);
        tv_sobuoc = (TextView) view.findViewById(R.id.tv_sobuoc);
        tv_quangduong = (TextView) view.findViewById(R.id.tv_quangduong);
        tv_kcal = (TextView) view.findViewById(R.id.tv_kcal);
        btn_luu = (Button) view.findViewById(R.id.btn_luu);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_nav);

        DB = new DBHelper(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, mStepCounter);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.unregisterListener(this, mStepCounter);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == mStepCounter) {
            demBuoc = (int) sensorEvent.values[0];
            tv_sobuoc.setText(String.valueOf(demBuoc));

            NguoiDung nd = DB.getChiSo(tenDangNhap);
            double chieuCao = nd.getChieuCao();
            double canNang = nd.getCanNang();
            if (chieuCao == 0 || canNang == 0) {
                Toast.makeText(getActivity().getApplication(), "Hãy nhập chiều cao và cân nặng để tính lượng tiêu thụ kcal!", Toast.LENGTH_SHORT).show();
            }
            double vanToc = 5;

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            double kcal = (0.035 * canNang) + ((vanToc * vanToc) / chieuCao) * 0.029 * canNang;
            tv_kcal.setText(String.valueOf(df.format(kcal)));
            double quangDuong = (demBuoc * 50) / 100;
            tv_quangduong.setText(String.valueOf(df.format(quangDuong)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void ThietLapThoiGian() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf.format(calendar.getTime());

        tv_thoigian.setText("Hôm nay: " + date);
    }
}