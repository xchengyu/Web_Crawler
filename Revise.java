import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;


public class Revise {
	private static final String TOTAL = "data/urls_NewsSite.csv";
	private static final String TOTAL_NEW = "data/urls_NewsSite_new.csv";
	private void readTotal() {
		BufferedReader br = null;
		FileWriter fr = null;
		try {
			String line = "";
			br = new BufferedReader(new FileReader(TOTAL));
			fr = new FileWriter(TOTAL_NEW);
			while ((line = br.readLine()) != null) {
				String[] tokens = line.trim().split(",");
				if (tokens.length == 2) {
					fr.write(line + "\n");
					continue;
				} else {
					int index = 0;
					for (int i = 0; i < tokens.length; i++) {
						if (tokens[i].equals("OK") || tokens[i].equals("N_OK")) {
							index = i;
							break;
						}
					}
					String newStr = "";
					for (int i = 0; i < index; i++) {
						newStr += tokens[i];
					}
					newStr += "," + tokens[index] + "\n";
					fr.write(newStr);
				}
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Revise re = new Revise();
		re.readTotal();
	}

}
