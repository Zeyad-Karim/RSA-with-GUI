import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RSAGUI extends JFrame {
    private JTextField pField, qField, plainTextField;
    private JTextArea cipherTextArea, decryptedTextArea;
    private JButton encryptButton, decryptButton, generateButton;
    private BigInteger p, q, n, phi, e, d;
    private int bitLength = 512; // Adjust the bit length for stronger key generation.
    private Random rand = new SecureRandom();

    public RSAGUI() {
        super("RSA Encryption Tool");
        createGUI();
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createGUI() {
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Prime p:"));
        pField = new JTextField(10);
        add(pField);

        add(new JLabel("Prime q:"));
        qField = new JTextField(10);
        add(qField);

        generateButton = new JButton("Generate Primes");
        generateButton.addActionListener(this::generatePrimes);
        add(generateButton);

        add(new JLabel("Plaintext:"));
        plainTextField = new JTextField(50);
        add(plainTextField);

        encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(this::encrypt);
        add(encryptButton);

        add(new JLabel("Ciphertext:"));
        cipherTextArea = new JTextArea();
        cipherTextArea.setEditable(false);
        add(new JScrollPane(cipherTextArea));

        decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(this::decrypt);
        add(decryptButton);

        add(new JLabel("Decrypted Text:"));
        decryptedTextArea = new JTextArea();
        decryptedTextArea.setEditable(false);
        add(new JScrollPane(decryptedTextArea));
    }

    private void generatePrimes(ActionEvent event) {
        p = BigInteger.probablePrime(bitLength, rand);
        q = BigInteger.probablePrime(bitLength, rand);
        pField.setText(p.toString());
        qField.setText(q.toString());
    }

    private void encrypt(ActionEvent event) {
        if (!initializeKey()) return;

        String plainText = plainTextField.getText();
        byte[] bytes = plainText.getBytes();
        BigInteger message = new BigInteger(1, bytes);

        if (message.compareTo(n) >= 0) {
            JOptionPane.showMessageDialog(this, "Plaintext is too large for the current key size.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigInteger cipher = message.modPow(e, n);
        cipherTextArea.setText(cipher.toString());
    }

    private void decrypt(ActionEvent event) {
        String cipherText = cipherTextArea.getText();
        BigInteger cipher = new BigInteger(cipherText);

        BigInteger plain = cipher.modPow(d, n);
        byte[] plainBytes = plain.toByteArray();
        // Handling potential leading zero issue (if any)
        if (plainBytes[0] == 0 && plainBytes.length > 1) {
            byte[] tmp = new byte[plainBytes.length - 1];
            System.arraycopy(plainBytes, 1, tmp, 0, tmp.length);
            plainBytes = tmp;
        }
        decryptedTextArea.setText(new String(plainBytes));
    }

    private boolean initializeKey() {
        try {
            p = new BigInteger(pField.getText());
            q = new BigInteger(qField.getText());
            n = p.multiply(q);
            phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
            e = BigInteger.valueOf(65537);  // Common choice for 'e'
            d = e.modInverse(phi);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for primes.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RSAGUI::new);
    }
}
