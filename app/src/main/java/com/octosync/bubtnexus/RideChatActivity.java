package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.octosync.bubtnexus.adapters.ChatAdapter;
import com.octosync.bubtnexus.models.Message;
import com.octosync.bubtnexus.models.ApiResponse;
import com.octosync.bubtnexus.models.MessageRequest;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideChatActivity extends AppCompatActivity {

    private int rideId;
    private List<Message> messages = new ArrayList<>();

    private RecyclerView recyclerViewMessages;
    private EditText etMessage;
    private ImageButton btnSend, btnBack;
    private ProgressBar progressBar;
    private TextView tvError;

    private ChatAdapter chatAdapter;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_chat);

        // Get ride ID from intent
        rideId = getIntent().getIntExtra("ride_id", 0);
        if (rideId == 0) {
            Toast.makeText(this, "Invalid ride", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sharedPrefManager = SharedPrefManager.getInstance(this);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        loadMessages();
    }

    private void initializeViews() {
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> sendMessage());

        // Send on enter key
        etMessage.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_ENTER &&
                    event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        int currentUserId = sharedPrefManager.getUserId();
        chatAdapter = new ChatAdapter(this, messages, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(chatAdapter);

        // Scroll to bottom when new message arrives
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                layoutManager.smoothScrollToPosition(recyclerViewMessages, null,
                        chatAdapter.getItemCount());
            }
        });
    }

    private void loadMessages() {
        String token = sharedPrefManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.getRideMessages("Bearer " + token, rideId, 1);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // Parse messages from response
                    // Note: You need to adjust this based on your actual API response structure
                    Object data = response.body().getData();
                    if (data instanceof List) {
                        // Assuming data is a list of messages
                        // You'll need to properly parse based on your API
                        showMessages(new ArrayList<Message>()); // Replace with actual parsing
                    }
                } else {
                    showError("Failed to load messages");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        String token = sharedPrefManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        // Clear input
        etMessage.setText("");

        // Create message request
        MessageRequest request = new MessageRequest(messageText);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.sendMessage("Bearer " + token, rideId, request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    // Message sent successfully
                    // You might want to add the message to the list immediately
                    // or wait for WebSocket/real-time update
                } else {
                    Toast.makeText(RideChatActivity.this,
                            "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RideChatActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMessages(List<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        chatAdapter.updateMessages(messages);
        recyclerViewMessages.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        recyclerViewMessages.setVisibility(View.GONE);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}