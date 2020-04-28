import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vocabularyapp.R;
import com.example.vocabularyapp.Vocabulary;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class VocabularyAdapter extends FirebaseRecyclerAdapter<Vocabulary, VocabularyAdapter.VocabularyHolder> {
    public Context context;

    public VocabularyAdapter(@NonNull FirebaseRecyclerOptions<Vocabulary> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull VocabularyHolder holder, int position, @NonNull Vocabulary model) {
        holder.vocabularyTextView.setText(model.getWord());
    }

    @NonNull
    @Override
    public VocabularyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.word_layout, parent, false);
        return new VocabularyHolder(view);
    }

    public class VocabularyHolder extends RecyclerView.ViewHolder {

        public TextView vocabularyTextView;

        public VocabularyHolder(@NonNull View itemView) {
            super(itemView);

            vocabularyTextView = itemView.findViewById(R.id.word_layout_textView);
        }
    }
}
