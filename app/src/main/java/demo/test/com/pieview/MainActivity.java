package demo.test.com.pieview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PieView pieView = (PieView) findViewById(R.id.pie_view);
        List<PieView.PieViewBean> list = new ArrayList<>();
        PieView.PieViewBean pieViewBean = new PieView.PieViewBean();
        pieViewBean.setRate("5.0");
        pieViewBean.setLegendColor("#ff00ff"); //紫色
        pieViewBean.setLegendName("钢铁");

        PieView.PieViewBean pieViewBean1 = new PieView.PieViewBean();
        pieViewBean1.setRate("30.0");
        pieViewBean1.setLegendColor("#ff5d00");//橙色
        pieViewBean1.setLegendName("煤炭");

        PieView.PieViewBean pieViewBean2 = new PieView.PieViewBean();
        pieViewBean2.setRate("16.0");
        pieViewBean2.setLegendColor("#ff1500"); //红色
        pieViewBean2.setLegendName("建筑");

        PieView.PieViewBean pieViewBean3 = new PieView.PieViewBean();
        pieViewBean3.setRate("39.0");
        pieViewBean3.setLegendColor("#00d8ff");//亮蓝色
        pieViewBean3.setLegendName("电子商务");

        PieView.PieViewBean pieViewBean4 = new PieView.PieViewBean();
        pieViewBean4.setRate("10.0");
        pieViewBean4.setLegendColor("#56e20b");//绿色
        pieViewBean4.setLegendName("银行");
        list.add(pieViewBean);
        list.add(pieViewBean1);
        list.add(pieViewBean2);
        list.add(pieViewBean3);
        list.add(pieViewBean4);

        pieView.setDatas(list);
    }
}
