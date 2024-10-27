import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.swing.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;

public class Main {
    public static void main(String[] args) {
        try {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select an XML file");


            int result = fileChooser.showOpenDialog(null);


            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + file.getAbsolutePath());


                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);


                document.getDocumentElement().normalize();


                Element root = document.getDocumentElement();
                System.out.println("Root Element: " + root.getNodeName());

                  handleCRUDOperations(document, root, file);

            } else {
                System.out.println("No file selected.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     public static void handleCRUDOperations(Document document, Element root, File file) throws Exception {
          StringBuilder content = new StringBuilder();
        content.append("Root Element: ").append(root.getNodeName()).append("\n\n");

         NodeList productList = document.getElementsByTagName("produit");

         String[] productNames = new String[productList.getLength()];

         for (int i = 0; i < productList.getLength(); i++) {
            Node productNode = productList.item(i);

            if (productNode.getNodeType() == Node.ELEMENT_NODE) {
                Element productElement = (Element) productNode;

                   String nom = productElement.getElementsByTagName("nom").item(0).getTextContent();
                String description = productElement.getElementsByTagName("description").item(0).getTextContent();
                String prix = productElement.getElementsByTagName("prix").item(0).getTextContent();
                String quantite = productElement.getElementsByTagName("quantite").item(0).getTextContent();

                 content.append("Product Name: ").append(nom).append("\n")
                        .append("Description: ").append(description).append("\n")
                        .append("Price: ").append(prix).append("\n")
                        .append("Quantity in stock: ").append(quantite).append("\n")
                        .append("------------------------------\n");


                productNames[i] = nom;
            }
        }

          JTextArea textArea = new JTextArea(20, 40);
        textArea.setText(content.toString());
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(null, scrollPane, "XML File Content", JOptionPane.INFORMATION_MESSAGE);

        String[] options = {"Add", "Update", "Delete", "Exit"};
        int choice = JOptionPane.showOptionDialog(null, "Choose an operation", "CRUD Operations", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0:
                  addProduct(document, root, file);
                break;
            case 1:
                 String selectedProduct = (String) JOptionPane.showInputDialog(null, "Select product to update", "Update Product", JOptionPane.QUESTION_MESSAGE, null, productNames, productNames[0]);
                if (selectedProduct != null) {
                    updateProduct(document, root, file, selectedProduct);
                }
                break;
            case 2:
                 selectedProduct = (String) JOptionPane.showInputDialog(null, "Select product to delete", "Delete Product", JOptionPane.QUESTION_MESSAGE, null, productNames, productNames[0]);
                if (selectedProduct != null) {
                    deleteProduct(document, root, file, selectedProduct);
                }
                break;
            case 3:
                System.exit(0);
                break;
        }
    }

      public static void addProduct(Document document, Element root, File file) throws Exception {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();

        Object[] message = {
                "Product Name:", nameField,
                "Description:", descriptionField,
                "Price:", priceField,
                "Quantity:", quantityField,
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Enter new product details", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String price = priceField.getText();
            String quantity = quantityField.getText();

            if (!name.isEmpty() && !description.isEmpty() && !price.isEmpty() && !quantity.isEmpty()) {
                Element newProduct = document.createElement("produit");

                Element nameElement = document.createElement("nom");
                nameElement.appendChild(document.createTextNode(name));
                newProduct.appendChild(nameElement);

                Element descriptionElement = document.createElement("description");
                descriptionElement.appendChild(document.createTextNode(description));
                newProduct.appendChild(descriptionElement);

                Element priceElement = document.createElement("prix");
                priceElement.appendChild(document.createTextNode(price));
                newProduct.appendChild(priceElement);

                Element quantityElement = document.createElement("quantite");
                quantityElement.appendChild(document.createTextNode(quantity));
                newProduct.appendChild(quantityElement);

                 root.appendChild(newProduct);

                saveXML(document, file);

                JOptionPane.showMessageDialog(null, "New product added successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

     public static void updateProduct(Document document, Element root, File file, String productName) throws Exception {
        NodeList productList = document.getElementsByTagName("produit");

        for (int i = 0; i < productList.getLength(); i++) {
            Node productNode = productList.item(i);
            if (productNode.getNodeType() == Node.ELEMENT_NODE) {
                Element productElement = (Element) productNode;
                String currentName = productElement.getElementsByTagName("nom").item(0).getTextContent();

                if (currentName.equals(productName)) {
                     JTextField nameField = new JTextField(productElement.getElementsByTagName("nom").item(0).getTextContent());
                    JTextField descriptionField = new JTextField(productElement.getElementsByTagName("description").item(0).getTextContent());
                    JTextField priceField = new JTextField(productElement.getElementsByTagName("prix").item(0).getTextContent());
                    JTextField quantityField = new JTextField(productElement.getElementsByTagName("quantite").item(0).getTextContent());

                    Object[] message = {
                            "Product Name:", nameField,
                            "Description:", descriptionField,
                            "Price:", priceField,
                            "Quantity:", quantityField,
                    };

                    int option = JOptionPane.showConfirmDialog(null, message, "Update product details", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        productElement.getElementsByTagName("nom").item(0).setTextContent(nameField.getText());
                        productElement.getElementsByTagName("description").item(0).setTextContent(descriptionField.getText());
                        productElement.getElementsByTagName("prix").item(0).setTextContent(priceField.getText());
                        productElement.getElementsByTagName("quantite").item(0).setTextContent(quantityField.getText());

                        saveXML(document, file);
                        JOptionPane.showMessageDialog(null, "Product updated successfully!");
                    }
                    break;
                }
            }
        }
    }

     public static void deleteProduct(Document document, Element root, File file, String productName) throws Exception {
        NodeList productList = document.getElementsByTagName("produit");

        for (int i = 0; i < productList.getLength(); i++) {
            Node productNode = productList.item(i);
            if (productNode.getNodeType() == Node.ELEMENT_NODE) {
                Element productElement = (Element) productNode;
                String currentName = productElement.getElementsByTagName("nom").item(0).getTextContent();

                if (currentName.equals(productName)) {

                    root.removeChild(productElement);
                    saveXML(document, file);
                    JOptionPane.showMessageDialog(null, "Product deleted successfully!");
                    break;
                }
            }
        }
    }


    public static void saveXML(Document document, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult resultStream = new StreamResult(new FileWriter(file));
        transformer.transform(source, resultStream);
    }
}
