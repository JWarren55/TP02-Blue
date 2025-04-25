package warren.aandp.project02.login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class LoginMethods {

    /*
        Order in which information is saved
        Email, Name, Salt, Password
     */
    final int SAVED_ID_LOCATION = 0;
    final int SAVED_EMAIL_LOCATION = 1;
    final int SAVED_NAME_LOCATION = 2;
    final int SAVED_SALT_LOCATION = 3;
    final int SAVED_PASSWORD_LOCATION = 4;

    //for encryption and decryption
    private static SecretKeySpec secretKey;
    private static byte[] key;
    static String secret = "four scores and 7 years ago?";
    public static String name = "no name";

    String studentPath = "src/main/resources/warren/aandp/project02/login/Student.txt";
    String professorPath = "src/main/resources/warren/aandp/project02/login/professor.txt";

    //User info text file
    //String filePath = "src\\main\\resources\\warren\\aandp\\project02\\login\\Student.txt";


    /*
            This Method is triggered by pressing Sign In
            It will same and encrypt email and name
     */
    public String addUser(String profession, String email, String name, String password) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        String finalID = "";

        //check for student of Professor --------------------------------------------------------------------------------
        String filePath;
        if (profession.equals("Student")) {
            filePath = studentPath;
            finalID += "S-";
        } else {
            filePath = professorPath;
            finalID += "P-";
        }

        //encrypt Email and Name for storage ------------------------------------------------------------------------------
        boolean emailInUse = false;
        var encryptedEmail = encrypt(email, secret);
        int newId = 0;

        //Find next id for student and check if email is not used --------------------------------------------------------
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //var for name and line read
            //String decryptName;
            String currentLine;
            String[] line;

            //go through every line
            //Find if email is in use, Find next id
            while ((currentLine = reader.readLine()) != null) {
                line = currentLine.split(",");
                String checkEmail = line[SAVED_EMAIL_LOCATION];
                if (checkEmail.equals(encryptedEmail)) {
                    emailInUse = true;
                    break;
                } else {
                    newId = Integer.parseInt(line[SAVED_ID_LOCATION].substring(2)) + 1;
                }
            }
            finalID += String.valueOf(newId);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
                // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //make salt and hash password ------------------------------------------------------------------------------------
        String salt = getSalt();
        var hashedPassword = getSecurePassword(password, salt);

        //write user in file ---------------------------------------------------------------------------------------------
        if (!emailInUse) {
            //adds new user
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                // Write the email, name, salt and password
                writer.write(finalID + "," + encryptedEmail + "," + name + "," + salt + "," + hashedPassword);
                writer.newLine();
                System.out.println("Account saved successfully");
                writer.close();
                return "signed up";
            } catch (IOException e) {
                System.out.println("An error occurred while saving the account: " + e.getMessage());
            }
        } else {
            return "email in use";
        }
        return "error";
    }

    /*--------------------------------------------------------------------------------------------------------------------

                           User Log in method

     -------------------------------------------------------------------------------------------------------------------*/

    public String loginUser(String profession, String email, String password) throws IOException {

        //Check for student or professor ---------------------------------------------------------------------------------
        String id = "no id found";
        //check for student of Proffessor
        String filePath;
        if (profession.equals("Student")) {
            filePath = studentPath;
        } else {
            filePath = professorPath;
        }

        //looks for user -------------------------------------------------------------------------------------------------
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //var for name and line read
            //String decryptName;
            String currentLine;
            String [] line;

            //go through every line
            while ((currentLine = reader.readLine()) != null) {
                //splits line into email, name, salt, password
                line = currentLine.split(",");
                //check for saved email-----------------------------------------------
                if (decrypt(line[SAVED_EMAIL_LOCATION], secret).equals(email)) {
                    //get name and decrypt----------
                    name = line[SAVED_NAME_LOCATION];
                    id = line[SAVED_ID_LOCATION];

                    //salt and password-----------------------------------------------
                    //get salt in Byte
                    String salt = line[SAVED_SALT_LOCATION];

                    //get saved password
                    var userPassword = line[SAVED_PASSWORD_LOCATION];
                    //salt given password to test vs saved
                    var testPassword = getSecurePassword(password, salt);

                    //test password
                    if (testPassword.equals(userPassword)) {
                        //name = decryptName;
                        return id; //----------------------------------------------------------Good return Here
                    } else {
                        return "Wrong Password";
                    }

                }
            }
        //errors
        } catch (IOException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // Return null if no match found
        return "Login Failed";
    }

    //Encryption, salt and hashing ---------------------------------------------------------------------------------------
    public static void setKey(String myKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest sha = null;
        key = myKey.getBytes("UTF-8");
        sha = MessageDigest.getInstance("SHA-512");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        secretKey = new SecretKeySpec(key, "AES");
    }

    //take in variable and key and output the encrypted form of the variable
    public static String encrypt(String strToEncrypt, String secret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        setKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] strToEncryptBytes = strToEncrypt.getBytes("UTF-8");
        byte[] finalCipher = cipher.doFinal(strToEncryptBytes);
        return Base64.getEncoder().encodeToString(finalCipher);
    }

    //take in variable and key and output the decrypted form of the variable
    public String decrypt(String strToDecrypt, String secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        setKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] finalByteString = Base64.getDecoder().decode(strToDecrypt);
        return new String(cipher.doFinal(finalByteString));
    }

    private static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    //gets password and salt and outputs hashed password
    private static String getSecurePassword(String passwordToHash, String stringSalt) throws NoSuchAlgorithmException {
        String generatedPassword = null;
        byte[] salt = Base64.getDecoder().decode(stringSalt);
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        byte[] bytes = md.digest(passwordToHash.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        generatedPassword = sb.toString();

        return generatedPassword;
    }
}