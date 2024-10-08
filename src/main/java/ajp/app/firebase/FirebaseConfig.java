package ajp.app.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("classpath:/rbjrg-app-firebase-adminsdk-servicekey.json")
    private Resource serviceKey;

    @Bean
    public FirebaseApp firebaseApp() {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(getCredentials())
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public Firestore firestore() {
        FirebaseApp firebaseApp = firebaseApp();
        return FirestoreClient.getFirestore(firebaseApp);
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        FirebaseApp firebaseApp = firebaseApp();
        return FirebaseAuth.getInstance(firebaseApp);
    }

    private GoogleCredentials getCredentials() {
        try {
            InputStream serviceAccount = new ByteArrayInputStream(serviceKey.getContentAsByteArray());
            return GoogleCredentials.fromStream(serviceAccount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load service key", e);
        }
    }

}
