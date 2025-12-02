package com.marketlink.fragments.ayuda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.marketlink.MainActivity;
import com.marketlink.R;

public class AyudaFragment extends Fragment {

    private MaterialCardView[] faqCards;
    private ImageView[] expandIcons;
    private TextView[] answerTexts;
    private boolean[] isExpanded;
    private MaterialButton btnChat;
    private MaterialButton btnCallSupport;
    private TextInputEditText etSearch;

    public AyudaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ayuda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Toolbar with Navigation Drawer
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null && toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            
            DrawerLayout drawerLayout = 
                ((MainActivity) getActivity()).getDrawerLayout();
            
            if (drawerLayout != null) {
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.dashboardFragment, R.id.pedidosListFragment, R.id.productCatalogFragment, 
                        R.id.empresaListFragment, R.id.userListFragment, R.id.reportesFragment,
                        R.id.configuracionFragment, R.id.ayudaFragment
                ).setOpenableLayout(drawerLayout).build();
                
                NavigationUI.setupWithNavController(toolbar, Navigation.findNavController(view), appBarConfiguration);
            }
        }

        // Initialize views
        etSearch = view.findViewById(R.id.et_search);
        btnChat = view.findViewById(R.id.btn_chat);
        btnCallSupport = view.findViewById(R.id.btn_call_support);

        // Initialize FAQ cards and expand icons
        faqCards = new MaterialCardView[]{
            view.findViewById(R.id.card_faq_1),
            view.findViewById(R.id.card_faq_2),
            view.findViewById(R.id.card_faq_3),
            view.findViewById(R.id.card_faq_4),
            view.findViewById(R.id.card_faq_5),
            view.findViewById(R.id.card_faq_6),
            view.findViewById(R.id.card_faq_7)
        };

        expandIcons = new ImageView[]{
            view.findViewById(R.id.iv_faq_1_expand),
            view.findViewById(R.id.iv_faq_2_expand),
            view.findViewById(R.id.iv_faq_3_expand),
            view.findViewById(R.id.iv_faq_4_expand),
            view.findViewById(R.id.iv_faq_5_expand),
            view.findViewById(R.id.iv_faq_6_expand),
            view.findViewById(R.id.iv_faq_7_expand)
        };

        answerTexts = new TextView[]{
            view.findViewById(R.id.tv_faq_1_answer),
            view.findViewById(R.id.tv_faq_2_answer),
            view.findViewById(R.id.tv_faq_3_answer),
            view.findViewById(R.id.tv_faq_4_answer),
            view.findViewById(R.id.tv_faq_5_answer),
            view.findViewById(R.id.tv_faq_6_answer),
            view.findViewById(R.id.tv_faq_7_answer)
        };

        isExpanded = new boolean[7];

        // Setup FAQ card click listeners
        for (int i = 0; i < faqCards.length; i++) {
            final int index = i;
            faqCards[i].setOnClickListener(v -> toggleFAQ(index));
        }

        // Search functionality
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            Toast.makeText(getContext(), "Buscando: " + etSearch.getText().toString(), Toast.LENGTH_SHORT).show();
            // TODO: Implement search logic
            return true;
        });

        // Chat button
        btnChat.setOnClickListener(v -> {
            // Simulate opening chat - in production, this would open a WebView or chat app
            Intent chatIntent = new Intent(Intent.ACTION_VIEW);
            chatIntent.setData(Uri.parse("https://wa.me/59170000000")); // WhatsApp link as example
            try {
                startActivity(chatIntent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Iniciando chat de soporte...", Toast.LENGTH_SHORT).show();
            }
        });

        // Call support button
        btnCallSupport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+59170000000")); // Placeholder phone number
            startActivity(intent);
        });
    }

    private void toggleFAQ(int index) {
        isExpanded[index] = !isExpanded[index];
        
        if (isExpanded[index]) {
            // Expand: Change icon to expand_less and show answer
            expandIcons[index].setImageResource(R.drawable.ic_expand_less);
            answerTexts[index].setVisibility(View.VISIBLE);
        } else {
            // Collapse: Change icon to expand_more and hide answer
            expandIcons[index].setImageResource(R.drawable.ic_expand_more);
            answerTexts[index].setVisibility(View.GONE);
        }
    }
}

