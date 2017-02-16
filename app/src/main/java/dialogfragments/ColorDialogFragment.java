package dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.widget.SeekBar;

import model.DoodleView;
import xyz.georgihristov.doodify.R;

/**
 * Created by gohv on 12.02.17.
 */

public class ColorDialogFragment extends android.support.v4.app.DialogFragment {
    private SeekBar alphaSeekBar;
    private  SeekBar redSeekBar;
    private  SeekBar greenSeekBar;
    private  SeekBar blueSeekBar;
    private View colorView;
    private int color;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View colorDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_color,null);
        builder.setView(colorDialogView);
        builder.setTitle(R.string.title_color_dialog);

        alphaSeekBar = (SeekBar)colorDialogView.findViewById(R.id.alphaSeekBar);
        redSeekBar = (SeekBar)colorDialogView.findViewById(R.id.redSeekBar);
        greenSeekBar = (SeekBar)colorDialogView.findViewById(R.id.greenSeekBar);
        blueSeekBar = (SeekBar)colorDialogView.findViewById(R.id.blueSeekBar);
        colorView = colorDialogView.findViewById(R.id.colorVIew);

        alphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        redSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        color  = doodleView.getDrawingColor();

        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        builder.setPositiveButton(R.string.button_set_color, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doodleView.setDrawingColor(color);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivityFragment fragment = getDoodleFragment();
        if(fragment != null){
            fragment.setDialogOnScreen(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivityFragment fragment = getDoodleFragment();
        if(fragment != null){
            fragment.setDialogOnScreen(false);
        }
    }

    private MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment)getFragmentManager().findFragmentById(R.id.doodleFragment);
    }

    private final SeekBar.OnSeekBarChangeListener colorChangedListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //user changed seekbar
                    if(fromUser){
                        color = Color.argb(alphaSeekBar.getProgress(),redSeekBar.getProgress(),
                                greenSeekBar.getProgress(),blueSeekBar.getProgress());
                        colorView.setBackgroundColor(color);
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };
}
