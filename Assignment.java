import javax.xml.parsers.*;
import java.io.*;
import org.w3c.dom.*;
import java.time.LocalDate;
import  java.time.format.DateTimeFormatter;


public class Assignment{
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory documentBuilder= DocumentBuilderFactory.newInstance();
            DocumentBuilder transformdocumentBuilder = documentBuilder.newDocumentBuilder();

            Document l1 = transformdocumentBuilder.parse(new File("License1.xml"));
            NodeList list1 = l1.getElementsByTagName("License");
            Document l2 = transformdocumentBuilder.parse(new File("License2.xml"));
            NodeList list2 = l2.getElementsByTagName("License");


            BufferedWriter invalidLicense = new BufferedWriter(new FileWriter("InvalidLicenseFile.txt"));
            BufferedWriter validLicense = new BufferedWriter(new FileWriter("ValidLicenseFile.txt"));
            BufferedWriter Merged = new BufferedWriter(new FileWriter("Merge.txt"));


            invalidLicense.write(LICENSE_HEADER_ROW);
            validLicense.write(LICENSE_HEADER_ROW);
            Merged.write(LICENSE_HEADER_ROW);

            for (int i = 0; i < list1.getLength(); i++) {
                Element license1 = (Element) list1.item(i);
                String k1 = getKey(license1);
                for (int j = 0; j < list2.getLength(); j++) {
                    Element license2 = (Element) list2.item(j);
                    String k2 = getKey(license2);
                    if (k1.equals(k2)) {
                        if (isValid(license1)) {
                            Merged.write("\n \n Valid License");
                            writeLicenseToFile(license1, validLicense);
                            writeLicenseToFile(license1, Merged);

                        } else {
                            Merged.write("\n \n InValid License");
                            writeLicenseToFile(license1, invalidLicense);
                            writeLicenseToFile(license1, Merged);

                        }
                        break;
                    }
                }
            }

            invalidLicense.close();
            validLicense.close();
            Merged.close();
        } catch (Exception e) {
            System.out.println("Failed");
        }
    }

    private static String getKey(Element license) {
        return license.getAttribute("NIPR_Number") + ","
                + license.getAttribute("State_Code") + ","
                + license.getAttribute("License_Number") + ","
                + license.getAttribute("License_Expiration_Date");
    }

    private static boolean isValid(Element license) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String expirationDateString = license.getAttribute("License_Expiration_Date");
        LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);
        return expirationDate.isAfter(currentDate) || expirationDate.isEqual(currentDate);
    }

    private static void writeLicenseToFile (Element license, BufferedWriter writer) throws IOException {
        String line = license.getAttribute("NIPR_Number") +
                license.getAttribute("License_Number") + ","
                + license.getAttribute("State_Code") + ","
                + license.getAttribute("Resident_Indicator") + ","
                + license.getAttribute("License_Class") + ","
                + license.getAttribute("License_Issue_Date") + ","
                + license.getAttribute("License_Expiration_Date") + ","
                + license.getAttribute("License_Status");
        NodeList loaTag = license.getElementsByTagName("LOA");
        for (int k = 0; k < loaTag.getLength(); k++) {
            Element loa = (Element) loaTag.item(k);
            String loaLine = line + ","
                    + loa.getAttribute("LOA_Name") + ","
                    + loa.getAttribute("LOA_Issue_Date") + ","
                    + loa.getAttribute("LOA_Expiration_Date") + ","
                    + loa.getAttribute("LOA_Status");
            writer.newLine();
            writer.write(loaLine);
        }
    }


    private static final String LICENSE_HEADER_ROW = "License ID,Jurisdiction,Resident,License Class,License Effective Date,License Expiry Date,License Status,License Line,License Line Effective Date,License Line Expiry Date,License Line Status";
}