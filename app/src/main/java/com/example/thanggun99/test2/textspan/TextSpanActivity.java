package com.example.thanggun99.test2.textspan;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thanggun99.test2.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextSpanActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view_textspan)
    RecyclerView recyclerViewTextSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_span);
        ButterKnife.bind(this);

        recyclerViewTextSpan.setHasFixedSize(true);
        recyclerViewTextSpan.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewTextSpan.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_textspan, parent, false));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                SimpleSpanBuilder simpleSpanBuilder = new SimpleSpanBuilder();

                simpleSpanBuilder.append("thanggun" + position,
                        new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(TextSpanActivity.this,
                                        "onClick benh nhan" + position, Toast.LENGTH_SHORT).show();
                            }
                        },
                        new StyleSpan(Typeface.BOLD),
                        new RelativeSizeSpan(1f),
                        new ForegroundColorSpan(Color.GREEN));

                simpleSpanBuilder.append(" - hỏi - ");

                simpleSpanBuilder.append("Đặng Nhân Ngoan" + position,
                        new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(TextSpanActivity.this,
                                        "onClick bac si" + position, Toast.LENGTH_SHORT).show();
                            }
                        },
                        new StyleSpan(Typeface.BOLD),
                        new RelativeSizeSpan(1f),
                        new ForegroundColorSpan(Color.YELLOW));


                TextView tvTitle = ((ViewHolder) holder).tvTitle;
                tvTitle.setText(simpleSpanBuilder.build());
                tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            public int getItemCount() {
                return 25;
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
