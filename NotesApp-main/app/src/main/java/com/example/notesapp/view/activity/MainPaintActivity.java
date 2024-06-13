package com.example.notesapp.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.ToolsListener;
import com.example.notesapp.Utils.Common;
import com.example.notesapp.adapter.ToolsAdapter;
import com.example.notesapp.databinding.ActivityMainPaintBinding;
import com.example.notesapp.model.PaintView;
import com.example.notesapp.model.ToolsItem;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainPaintActivity extends AppCompatActivity implements ToolsListener {

    ActivityMainPaintBinding binding;
    ToolsAdapter toolsAdapter;
    PaintView paintView;
    int colorBackground, colorBrush, eraserSize, brushSize;
    Intent intent;
    SharedPreferences sharedPreferences;
    String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_paint);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityMainPaintBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        intent = new Intent(new Intent(MainPaintActivity.this, ScheduleDetailsActivity.class));

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        docId = sharedPreferences.getString("docId", "");

        setUpRecycleView();

        binding.btnCheckPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transfertoImage();
                finish();
            }
        });

        binding.btnBackPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

    }

    private Bitmap getBitmapFromPaintView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void transfertoImage() {
        Bitmap bitmap = getBitmapFromPaintView(binding.paintView);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        intent.putExtra("imagePaint", bytes);
        intent.putExtra("docId", docId);

        startActivity(intent);
    }

    public void setUpRecycleView() {

        colorBackground = Color.WHITE;
        colorBrush = Color.BLACK;
        eraserSize = brushSize = 12;
        paintView = findViewById(R.id.paint_view);

        binding.recyclerViewPaintTools.setHasFixedSize(true);
        binding.recyclerViewPaintTools.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        toolsAdapter = new ToolsAdapter(getListTools(), this);
        binding.recyclerViewPaintTools.setAdapter(toolsAdapter);
    }


    public List<ToolsItem> getListTools() {
        List<ToolsItem> listPaint = new ArrayList<>();

        listPaint.add(new ToolsItem(R.drawable.baseline_brush_24, Common.BRUSH));
        listPaint.add(new ToolsItem(R.drawable.baseline_delete_sweep_24, Common.ERASER));
        listPaint.add(new ToolsItem(R.drawable.baseline_palette_24, Common.COLORS));
        listPaint.add(new ToolsItem(R.drawable.baseline_format_paint_24, Common.FILL));
        listPaint.add(new ToolsItem(R.drawable.baseline_undo_24, Common.UNDO));

        return listPaint;
    }

    @Override
    public void onSelected(String name) {

        switch (name) {
            case Common.BRUSH:
                paintView.disableEraser();
                showDialogSize(false);
                break;

            case Common.ERASER:
                paintView.enableEraser();
                showDialogSize(true);
                break;

            case Common.UNDO:
                paintView.returnLastAction();
                break;

            case Common.FILL:
                updateColor(name);
                break;

            case Common.COLORS:
                updateColor(name);
                break;
        }

    }

    private void updateColor(String name) {

        int color;

        if (name.equals(Common.FILL)) {
            color = colorBackground;
        } else {
            color = colorBrush;
        }

        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(color)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                        if (name.equals(Common.FILL)) {
                            colorBackground = lastSelectedColor;
                            paintView.setColorBackground(colorBackground);
                        } else {
                            colorBrush = lastSelectedColor;
                            paintView.setBrushColor(colorBrush);
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).build().show();
    }

    private void showDialogSize(boolean isEraser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog, null, false);

        TextView toolsSelected = view.findViewById(R.id.status_tools_selected);
        TextView statusSize = view.findViewById(R.id.status_size);
        ImageView imageTools = view.findViewById(R.id.imgView_tools);
        SeekBar seekBar = view.findViewById(R.id.seekbar_size);
        seekBar.setMax(99);

        if (isEraser) {
            toolsSelected.setText("Eraser size");
            imageTools.setImageResource(R.drawable.baseline_delete_sweep_black);
            statusSize.setText("Selected size: " + eraserSize);
        } else {
            toolsSelected.setText("Brush size");
            imageTools.setImageResource(R.drawable.baseline_brush_black);
            statusSize.setText("Selected size: " + brushSize);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (isEraser) {
                    eraserSize = progress + 1;
                    statusSize.setText("Selected size: " + eraserSize);
                    paintView.setSizeEraser(eraserSize);
                } else {
                    brushSize = progress + 1;
                    statusSize.setText("Selected size: " + brushSize);
                    paintView.setSizeBrush(brushSize);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setView(view);
        builder.show();
    }

}