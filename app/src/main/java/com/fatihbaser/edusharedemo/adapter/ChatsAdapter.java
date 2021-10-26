package com.fatihbaser.edusharedemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.fatihbaser.edusharedemo.R;
import com.fatihbaser.edusharedemo.activities.ChatActivity;
import com.fatihbaser.edusharedemo.models.Chat;
import com.fatihbaser.edusharedemo.providers.AuthProvider;
import com.fatihbaser.edusharedemo.providers.ChatsProvider;
import com.fatihbaser.edusharedemo.providers.MessagesProvider;
import com.fatihbaser.edusharedemo.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessagesProvider mMessagesProvider;
    ListenerRegistration mListener;
    ListenerRegistration mListenerLastMessage;
//    BottomNavigationView bottomNavigationView;

    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Chat chat) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();
        if (mAuthProvider.getUid().equals(chat.getIdUser1())) {
            getUserInfo(chat.getIdUser2(), holder);
        } else {
            getUserInfo(chat.getIdUser1(), holder);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatActivity(chatId, chat.getIdUser1(), chat.getIdUser2());
            }
        });

        getLastMessage(chatId, holder.textViewLastMessage);

        String idSender = "";
        if (mAuthProvider.getUid().equals(chat.getIdUser1())) {
            idSender = chat.getIdUser2();
        } else {
            idSender = chat.getIdUser1();
        }
        getMessageNotRead(chatId, idSender, holder.textViewMessageNotRead, holder.frameLayoutMessageNotRead);

    }

    private void getMessageNotRead(String chatId, String idSender, final TextView textViewMessageNotRead, final FrameLayout frameLayoutMessageNotRead) {

        mListener = mMessagesProvider.getMessagesByChatAndSender(chatId, idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int size = queryDocumentSnapshots.size();
                    if (size > 0) {
//                        bottomNavigationView = findViewById(R.id.bottom_navigation);
//                        Menu menu = bottomNavigationView.getMenu();
//                        menu.findItem(R.id.itemChats).setIcon(R.drawable.outline_mark_chat_unread_24);
                        frameLayoutMessageNotRead.setVisibility(View.VISIBLE);
                        textViewMessageNotRead.setText(String.valueOf(size));

                    } else {
                        frameLayoutMessageNotRead.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    public ListenerRegistration getListener() {
        return mListener;
    }

    public ListenerRegistration getListenerLastMessage() {
        return mListenerLastMessage;
    }

    private void getLastMessage(String chatId, final TextView textViewLastMessage) {
        mListenerLastMessage = mMessagesProvider.getLastMessage(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int size = queryDocumentSnapshots.size();
                    if (size > 0) {
                        String lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                        textViewLastMessage.setText(lastMessage);
                    }
                }
            }
        });
    }

    private void goToChatActivity(String chatId, String idUser1, String idUser2) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat", chatId);
        intent.putExtra("idUser1", idUser1);
        intent.putExtra("idUser2", idUser2);
        context.startActivity(intent);
    }

    private void getUserInfo(String idUser, final ViewHolder holder) {
        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText(username.toUpperCase());
                    }
                    if (documentSnapshot.contains("image")) {
                        String imageProfile = documentSnapshot.getString("image");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(context).load(imageProfile).into(holder.circleImageChat, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.bar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.circleImageChat.setImageResource(R.drawable.ic_baseline_error_24);
                                        holder.bar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewLastMessage;
        TextView textViewMessageNotRead;
        CircleImageView circleImageChat;
        FrameLayout frameLayoutMessageNotRead;
        View viewHolder;
        ProgressBar bar;
        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUsernameChat);
            textViewLastMessage = view.findViewById(R.id.textViewLastMessageChat);
            textViewMessageNotRead = view.findViewById(R.id.textViewMessageNotRead);
            circleImageChat = view.findViewById(R.id.circleImageChat);
            frameLayoutMessageNotRead = view.findViewById(R.id.frameLayoutMessageNotRead);
            bar = view.findViewById(R.id.postLoading);
            viewHolder = view;

        }
    }

}
