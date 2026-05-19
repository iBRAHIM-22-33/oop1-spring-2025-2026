package fileio;

import entity.Cow;
import java.io.*;

public class CowFileIO {

    private static final String FILE_NAME = "entry.txt";
    private static final String TEMP_FILE = "temp.txt";

 public static void createFileIfNotExists() throws IOException {
        File file = new File(FILE_NAME); 

        
        if (!file.exists())
            file.createNewFile(); 
    }

    public static boolean tagIdExists(String tagId) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Cow c = Cow.fromLine(line);
                if (c != null && c.getTagId().equals(tagId)) {
                    return true;
                }
            }
        } catch (IOException ignored) {}
        return false;
    }

    public static int countRecords() {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException ignored) {}
        return count;
    }

    public static void addCow(Cow c) throws IOException {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
            pw.println(c.toLine());
        }
    }

    public static boolean updateCow(Cow c) throws IOException {
        File inputFile = new File(FILE_NAME);
        File tempFile = new File(TEMP_FILE);
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                Cow existing = Cow.fromLine(line);
                if (existing != null && existing.getTagId().equals(c.getTagId())) {
                    bw.write(c.toLine());
                    found = true;
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
        }

        if (found) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                throw new IOException("Failed to finalize cow update.");
            }
        } else {
            tempFile.delete();
        }
        return found;
    }

    public static boolean deleteCow(String tagId) throws IOException {
        File inputFile = new File(FILE_NAME);
        File tempFile = new File(TEMP_FILE);
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                Cow existing = Cow.fromLine(line);
                if (existing != null && existing.getTagId().equals(tagId)) {
                    found = true;
                    continue; 
                }
                bw.write(line);
                bw.newLine();
            }
        }

        if (found) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                throw new IOException("Failed to finalize cow deletion.");
            }
        } else {
            tempFile.delete();
        }
        return found;
    }

    public static Object[][] getAllCows() {
        int total = countRecords();
        Object[][] rows = new Object[total][4];
        int idx = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null && idx < total) {
                Cow c = Cow.fromLine(line);
               if (c != null) {
                    Object[] row = c.toRow();
                    rows[idx][0] = row[0];   
                    rows[idx][1] = row[1];   
                    rows[idx][2] = row[2];   
                    rows[idx][3] = row[3];   
                    idx++;                   
                }
            }
        } catch (IOException ignored) {}
        return rows;
    }

    public static Object[][] searchCows(String keyword) {
        String kw = keyword.toLowerCase();
        int matchCount = 0;

        // Pass 1: Count Matches
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Cow c = Cow.fromLine(line);
                if (c != null && (c.getTagId().toLowerCase().contains(kw) || c.getBreed().toLowerCase().contains(kw))) {
                    matchCount++;
                }
            }
        } catch (IOException ignored) {}

        // Pass 2: Populate matches
        Object[][] results = new Object[matchCount][4];
        int idx = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null && idx < matchCount) {
                Cow c = Cow.fromLine(line);
                if (c != null && (c.getTagId().toLowerCase().contains(kw)
                  || c.getBreed().toLowerCase().contains(kw))) {
                  Object[] row = c.toRow(); 
                   results[idx][0] = row[0]; 
                    results[idx][1] = row[1]; 
                    results[idx][2] = row[2]; 
                    results[idx][3] = row[3]; 
                     idx++; 
                }
            }
        } catch (IOException ignored) {}
        return results;
    }
}