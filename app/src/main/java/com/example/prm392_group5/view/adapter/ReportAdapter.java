package com.example.prm392_group5.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    public interface ReportActionListener {
        void onEditReport(Report report);
        void onDeleteReport(Report report);
    }

    public interface OnReportClickListener {
        void onReportClick(Report report);
    }

    private List<Report> reportList;
    private ReportActionListener actionListener;
    private OnReportClickListener clickListener;

    public ReportAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    public void setReportActionListener(ReportActionListener listener) {
        this.actionListener = listener;
    }

    public void setOnReportClickListener(OnReportClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.bind(report);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void updateReports(List<Report> newReports) {
        this.reportList = newReports;
        notifyDataSetChanged();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReportContent, tvReportBy, tvReportDate;
        private Button btnEdit, btnDelete;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportContent = itemView.findViewById(R.id.tvReportContent);
            tvReportBy = itemView.findViewById(R.id.tvReportBy);
            tvReportDate = itemView.findViewById(R.id.tvReportDate);
            btnEdit = itemView.findViewById(R.id.btnEditReport);
            btnDelete = itemView.findViewById(R.id.btnDeleteReport);
        }

        public void bind(Report report) {
            tvReportContent.setText(report.content);
            tvReportBy.setText("By: " + report.reportBy);
            
            // Format timestamp to readable date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            tvReportDate.setText(sdf.format(new Date(report.timestamp)));

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onReportClick(report);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditReport(report);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteReport(report);
                }
            });
        }
    }
}