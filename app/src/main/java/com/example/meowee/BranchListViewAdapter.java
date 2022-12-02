package com.example.meowee;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class BranchListViewAdapter extends BaseAdapter {
    final ArrayList<Branch> branches;

    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat h_sdf = new SimpleDateFormat("HH");
    SimpleDateFormat m_sdf = new SimpleDateFormat("mm");
    int h = Integer.parseInt(h_sdf.format(currentTime));
    int m = Integer.parseInt(m_sdf.format(currentTime));

    BranchListViewAdapter(ArrayList<Branch> array) {
        this.branches = array;
    }

    @Override
    public int getCount() {
        //Trả về tổng số phần tử, nó được gọi bởi ListView
        return branches.size();
    }

    @Override
    public Object getItem(int position) {
        //Trả về dữ liệu ở vị trí position của Adapter, tương ứng là phần tử
        //có chỉ số position trong listProduct
        return branches.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //convertView là View của phần tử ListView, nếu convertView != null nghĩa là
        //View này được sử dụng lại, chỉ việc cập nhật nội dung mới
        //Nếu null cần tạo mới

        View viewProduct;
        if (convertView == null) {
            viewProduct = View.inflate(parent.getContext(), R.layout.fragment_map_branch_detail, null);
        } else viewProduct = convertView;

        //Bind sữ liệu phần tử vào View
        Branch branch = (Branch) getItem(position);
        ((TextView) viewProduct.findViewById(R.id.branch_name)).setText(String.format("%s", branch.name));
        ((TextView) viewProduct.findViewById(R.id.branch_phoneNumber)).setText(String.format("Số điện thoại: %s", branch.phoneNumber));
        TextView state = viewProduct.findViewById(R.id.branch_state);
        TextView else_state = viewProduct.findViewById(R.id.branch_else_state);

        ArrayList<String> res = branch.getFormat(this.h, this.m);

        state.setText(res.get(0));
        else_state.setText(res.get(1));

        if (res.get(0).equals("Open")) {
            state.setTextColor(Color.parseColor("#22B14C"));
        }
        else {
            state.setTextColor(Color.parseColor("#FF0000"));
        }

        return viewProduct;
    }
}
