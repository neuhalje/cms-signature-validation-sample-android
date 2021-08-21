package name.neuhalfen.cmssignaturevalidation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import name.neuhalfen.cmssignaturevalidation.databinding.FragmentSecondBinding;

import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        binding.buttonDoValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> tests = new CMSValidationTest().runTests();
                for (String t : tests) {
                    log(t + "\n--------\n\n");
                }
            }
        });
    }

    private void log(String message){
        binding.textviewSecond.setText(binding.textviewSecond.getText() + "\n"  + message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}