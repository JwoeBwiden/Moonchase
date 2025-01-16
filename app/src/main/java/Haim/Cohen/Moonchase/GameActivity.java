package Haim.Cohen.Moonchase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of your Game class and set it as the content view
        Game game = new Game(this);
        setContentView(game);
    }
}
