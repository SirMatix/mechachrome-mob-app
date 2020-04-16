package com.example.mechachromemobileapp.Activities.BookSale;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mechachromemobileapp.Adapters.BookSaleAdapter;
import com.example.mechachromemobileapp.Models.BookSaleModel;
import com.example.mechachromemobileapp.Models.CustomItemAnimation;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class BookSale extends AppCompatActivity {

    private final String TAG = "BookSale";

    private BookSaleAdapter bookSaleAdapter;
    private FloatingActionButton addBookButton;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference booksReference = fStore.collection("books_for_sale");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_sale);

        initViews();
        buildBookSaleRecyclerView(bookSaleAdapter);
        setButtons();
    }

    private void initViews() {
        // Adapter initialization
        bookSaleAdapter = getAdapter();

        // Layout buttons initialization
        addBookButton = findViewById(R.id.button_add_book);
    }

    private void addBook() {
        startActivity(new Intent(getApplicationContext(), AddSaleBook.class));
        finish();
    }

    private void setButtons() {
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });

        bookSaleAdapter.setOnItemClickListener(new BookSaleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                BookSaleModel sellBook = documentSnapshot.toObject(BookSaleModel.class);
                if(!sellBook.isSold()){
                    Intent intent = new Intent(getApplicationContext(), BookSalePage.class);
                    intent.putExtra("book_title", sellBook.getTitle());
                    intent.putExtra("book_id", documentSnapshot.getId());
                    startActivity(intent);
                }
            }
        });
    }

    private BookSaleAdapter getAdapter() {
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        if(userID == null) {
            Query query = booksReference.orderBy("addDate", Query.Direction.ASCENDING);
            FirestoreRecyclerOptions<BookSaleModel> options = new FirestoreRecyclerOptions.Builder<BookSaleModel>()
                    .setQuery(query, BookSaleModel.class)
                    .build();
            return new BookSaleAdapter(options);
        } else {
            Query query = booksReference.whereEqualTo("seller_id", userID).orderBy("addDate", Query.Direction.ASCENDING);
            FirestoreRecyclerOptions<BookSaleModel> options = new FirestoreRecyclerOptions.Builder<BookSaleModel>()
                    .setQuery(query, BookSaleModel.class)
                    .build();
            return new BookSaleAdapter(options);
        }
    }

    private void buildBookSaleRecyclerView(BookSaleAdapter adapter) {
        RecyclerView bookSaleRecyclerView;
        bookSaleRecyclerView = findViewById(R.id.booksale_recycler_view);
        bookSaleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookSaleRecyclerView.setHasFixedSize(true);
        bookSaleRecyclerView.setItemAnimator(new CustomItemAnimation());
        bookSaleRecyclerView.setAdapter(adapter);

        /*
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                bookSaleAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(bookSaleRecyclerView);

         */

    }

    @Override
    protected void onStart() {
        super.onStart();
        bookSaleAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bookSaleAdapter.stopListening();
    }
}
