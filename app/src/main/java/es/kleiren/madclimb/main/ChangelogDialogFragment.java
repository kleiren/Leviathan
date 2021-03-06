package es.kleiren.madclimb.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import es.kleiren.madclimb.R;

public class ChangelogDialogFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_changelog_dialog, container, false);
        String text = ((MainActivity) getActivity()).updates;
        text = text.replace("\\\n", "\n");
        ((TextView)view.findViewById(R.id.text_updates)).setText(text);
        return view;
    }
}
