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

    private final String TAG = "TAG";
    private RecyclerView bookSaleRecyclerView;
    private BookSaleAdapter bookSaleAdapter;
    private FloatingActionButton addBookButton;
    private FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_sale);

        initViews();

        buildBookSaleRecyclerView();

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

    private void initViews() {
        // Firebase init
        fStore = FirebaseFirestore.getInstance();

        // layout elements init
        addBookButton = findViewById(R.id.button_add_book);

        // recyclerView init
        bookSaleRecyclerView = findViewById(R.id.booksale_recycler_view);
        bookSaleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookSaleRecyclerView.setHasFixedSize(true);
        bookSaleRecyclerView.setItemAnimator(new CustomItemAnimation());

        // buttons on click listeners
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }

            private void addBook() {
                /*
                adding the book will work on principle that new activity is opened on which you add
                information about a book and that data is being send to the firestore to store it
                after successful operation admin will go back to previous activity and get a toast
                about successful adding of a book (this is admin only functionality)
                 */
                startActivity(new Intent(getApplicationContext(), AddSaleBook.class));
                finish();
            }
        });
    }


    private void buildBookSaleRecyclerView() {
        CollectionReference booksReference = fStore.collection("books_for_sale");
        // query to display newest books for sale first
        Query query = booksReference.orderBy("addDate", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<BookSaleModel> options = new FirestoreRecyclerOptions.Builder<BookSaleModel>()
                .setQuery(query, BookSaleModel.class)
                .build();

        bookSaleAdapter = new BookSaleAdapter(options);
        bookSaleRecyclerView.setHasFixedSize(true);
        bookSaleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookSaleRecyclerView.setAdapter(bookSaleAdapter);

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
