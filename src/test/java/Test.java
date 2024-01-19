import com.sandun.web.entities.User;
import com.sandun.web.entities.UserType;
import com.sandun.web.service.UserService;
import com.sandun.web.service.UserTypeService;
import com.sandun.web.util.HibernateUtil;
import com.sandun.web.util.JavaMailUnit;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Test {
    public static void main(String[] args) {
        String inputString = "bd3bbc6c-c091-485c-96fa-1b90ffb2bf6e";

        // Remove all alphabetic characters using regular expression
        String resultString = inputString.replaceAll("[a-zA-Z-]", "");
        if(resultString.length()>6){
            resultString = resultString.substring(0,5);
        }
        System.out.println("Original String: " + inputString);
        System.out.println("String without alphabets: " + resultString);
    }
}
