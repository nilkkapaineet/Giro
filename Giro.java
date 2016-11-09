import java.util.Scanner; 
//package textfiles;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;
//package arraylist;
import java.util.ArrayList;

class Giro{  
    public static void main(String args[]) throws IOException {  
      
      // globaaleja muuttujia
      int pelaajaMaara;
      final int kuskienMaara = 45;
      final int kuskienMaaraJoukkueessa = 10;
      int rivimaara=0; // täällä on tiedostossa olevien rivien määrä, voidaan korvata, pitää muuttaa
      final int maksimihinta = 10000;
      final int etappienMaara = 3;
      
      Kuski[] kuski = new Kuski[kuskienMaara]; 
      String fileName = "ajajatTesti.dat";  // tiedosto, jossa on kaikki kuskit riveittäin ja tiedot #-merkillä erotettuina
      try { // luetaan tiedosto rivi riviltä
        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          stringBuffer.append(line);
          stringBuffer.append("\n");
        }
        fileReader.close();
        System.out.println("Contents of file:");
        System.out.println(stringBuffer.toString());
        
        // erotellaan tässä nuo regEx merkein varustettu luettu tiedosto
        Pattern p = Pattern.compile("\\#.*?\\#");
        Matcher m = p.matcher(stringBuffer.toString());
        int i=0;
        int nro1=0;
        String nimi1="";
        int hinta1=0;
        String tiimi1="";
        while(m.find())
        {
          CharSequence rivi = m.group().subSequence(1, m.group().length()-1);
          String tekstirivi = rivi.toString();
          
          // riveittäin löydökset paikalleen
          if (i%4 == 0) {
            // kilpailunumero
            nro1 = Integer.parseInt(tekstirivi);           
          }
          if (i%4 == 1) {
            // nimi
            nimi1 = tekstirivi;
          }
          if (i%4 == 2) {
            // hinta
            hinta1 = Integer.parseInt(tekstirivi);
          }
          if (i%4 == 3) {
            // joukkue
            tiimi1 = tekstirivi;
            kuski[rivimaara] = new Kuski(nro1, nimi1, tiimi1, hinta1);
            kuski[rivimaara].tulostaKuski();
            rivimaara++; // nyt vaihtuu kuski, indeksillä hoitoon 
            System.out.println("rivi: " + i);
          }
          i++;
        }
        // regEx käsittely loppuu
        
      } catch (IOException e) {
        e.printStackTrace();
      }
    
      
      // Siirrytään käyttäjän syötteen vastaanottamiseen
      Scanner s = new Scanner ( System.in ); // luetaan käyttäjän syöte
      System.out.println("Tervetuloa pelaamaan Ranskan ympäriajon fantasiamanageria"); 
      System.out.print("Montako pelaajaa tulee mukaan? ");
      pelaajaMaara = Integer.parseInt(s.next() );
      Joukkue[] joukkue = new Joukkue[pelaajaMaara];
     
      // testaillaan metodien toimivuutta
     // kuski[0] = new Kuski(1, "Alberto Contador", "Tinkoff", 3000);
     // kuski[1] = new Kuski(2, "Chris Froome", "Sky", 3500);
             
      for(int i=0;i<kuskienMaara;i++){
        kuski[i].tulostaKuski();
      }
      
      for(int i=0;i<pelaajaMaara;i++) { // tässä pyöritään pelaajamäärän verran
        System.out.print("Kirjoita nimesi: ");
        String nimi = s.next();
        System.out.print("Anna joukkueellesi nimi: ");
        String tiimi = s.next();
        joukkue[i] = new Joukkue(tiimi);
        
        // lisätään alkuperäiset jäsenet tiimille
        System.out.println("Hyvä " + nimi + ". Anna tiimillesi " + tiimi + " alkuperäisten kuskien numerot yksi kerrallaan. Syöttämällä '0' nollaat kaikki valinnat.");
        // väliaikainen taulu siltä varalta, ettei samaa kuskia valita kahdesti
        int[] valitutKuskit = new int[kuskienMaaraJoukkueessa];
        for(int j=0;j<kuskienMaaraJoukkueessa;j++){
          valitutKuskit[j] = 0;
        }
        for(int j=0;j<kuskienMaaraJoukkueessa;j++) {
          boolean nollattiinkoJoukkue = false;
          int valitunKuskinNro = Integer.parseInt(s.next() );
          if (valitunKuskinNro == 0) {
            // nollaa
            for(int k=0;k<kuskienMaaraJoukkueessa;k++) {
              valitutKuskit[k] = 0;
            }
            j=-1;
            joukkue[i].alustaJoukkue();
          } else {
            // tarkistetaan tuplakuskin varalta
            boolean eiSaaVaihtaa = true;
            for(int kk=0;kk<kuskienMaaraJoukkueessa;kk++) {
              if(valitutKuskit[kk] == valitunKuskinNro) {
                eiSaaVaihtaa = false;
              }
            }
            if (eiSaaVaihtaa) {
              if(joukkue[i].lisaaKuski(kuski[valitunKuskinNro-1], valitunKuskinNro, 1) ){
                valitutKuskit[j] = valitunKuskinNro;
              //  System.out.print("Lisättiin: ");
                kuski[valitunKuskinNro-1].tulostaKuski();
              } else {
                System.out.println("Problem");
                j--;
              }
            } else {
              System.out.println("Kuski on joukkueessa, valitse toinen.");
              j--;
            }
          }
        } // kuskimäärä
        joukkue[i].nollaaVaihdot(); // ekalla kierroksella ei pidä kulua vaihtoja
        joukkue[i].tulostaJoukkue();
        
      } // pelaajamäärä
      
      // varsinaiset ajokierrokset
      for (int i=0;i<etappienMaara;i++) {
      
        Random rnd = new Random();
        // ja sitten varsinaiset etapit
        // arvotaan ensin kierroksen voittajat
        // samaa ei saa arpoa uudelleen
        
        // ----------jonkinlaisen painotuksen hinnan perusteella tähän arvontaan olisi hyvä saada -------
        
        int tamaEtappi = i+1;
        System.out.println("-------------------- Etappi " + tamaEtappi + " ----------------------------");
        ArrayList<Integer> voitot = new ArrayList<Integer>();
        int[] etappiPinnat = {100,70,50,35,30,25,20,16,13,10,7,5,3,2,1};
        int r=0;
        System.out.println("Etapin lopputulos kuskinumeroittain: ");
        while (voitot.size() < 15) { // kuinka monta kuskia arvotaan voittajaksi
          int random = rnd.nextInt(kuskienMaara)+1; 
          if (!voitot.contains(random)) {
            voitot.add(random);
            kuski[random-1].lisaaPisteet(etappiPinnat[r] );
            r++;
            System.out.print(random + ", ");
          }
        }
        System.out.println("");
        
        // paitojen arvonta
        ArrayList<Integer> paidat = new ArrayList<Integer>();
        System.out.println("Paidat etapilla: ");
        int[] etappiPaidat={25,20,20,15};
        r=0;
        while(paidat.size() < 4) {
          int random = rnd.nextInt(kuskienMaara)+1;
          if(!paidat.contains(random)) {
            kuski[random-1].lisaaPisteet(etappiPaidat[r] );
            r++;
            paidat.add(random);
            System.out.print(random + ", ");
          }
        }
        System.out.println("");
        
        boolean ekaPelaaja = true; // tässä menee tieto siitä, onko kyseessä eka joukkue, jolloin kuskeille henkkoht pinnat, muuten ei
        
        // etapilla olleet paidat
        // public void lisaaPaitaPisteet(ArrayList<Integer> paidat, boolean ekaPelaaja) {
        
        
        // etapin voittavan tiimin arvonta
        // arvotaan yksi kuski ja otetaan sen tiimi
        int etapinTiimi = rnd.nextInt(kuskienMaara)+1;
        String etappiTiimi = kuski[etapinTiimi].annaTiimi();
        ekaPelaaja = true;
        System.out.println("Etapin paras joukkue oli " + etappiTiimi);
        for(int j=0;j<pelaajaMaara;j++) {
          // public void lisaaTiimiPisteet(String tiimi, boolean ekaPelaaja) {
          joukkue[j].lisaaTiimiPisteet(etappiTiimi, ekaPelaaja);
          ekaPelaaja = false;
        }
        
        // etapin paras irtiottaja
        ekaPelaaja = true;
        int etapinIrtiotto = rnd.nextInt(kuskienMaara)+1;
        System.out.println("Irtiotossa pisimpään oli kuski nro " + etapinIrtiotto);
        // kuskille kertaalleen irtiottopinnat
        for(int j=0;j<pelaajaMaara;j++) {
          joukkue[j].lisaaIrtiottoPisteet(etapinIrtiotto, ekaPelaaja);
          ekaPelaaja = false;
        }
        
        // lasketaan pisteet
        ekaPelaaja = true; 
        for(int j=0;j<pelaajaMaara;j++) {
          // ensin pisteet etapin kuskien järjestyksestä
          int kierrospinnat = joukkue[j].laskeKierrosPisteet(voitot, ekaPelaaja);
          // paitapinnat 25-20-20-15
          // paras joukkue 5 pts / kuski
          // pisin irtiotto 10 pts
          System.out.println("Etappi antoi sijoituksista tiimille " + joukkue[j].annaNimi() + ": " + kierrospinnat);
          System.out.println("Pinnoja yhteensä: " + joukkue[j].annaPinnat() );
          ekaPelaaja = false;
        }
        
        // pelaajien määrällä luupattava
        for(int j=0;j<pelaajaMaara;j++) {
        
          joukkue[j].tulostaJoukkue();
          // kysytään vaihdot
          boolean tulikoVaihto = true;
          do {
            System.out.println(joukkue[j].annaNimi() + " Haluatko (v)aihtaa vai (o)hittaa?");
            String vaihdetaanko = s.next();
            if(vaihdetaanko.equals("v") ) {
            
              System.out.print("Anna poistettavan kuskin numero: ");
              boolean onnistuikoPoisto = true;
              do {
                int poistokuski = Integer.parseInt(s.next() );
                if(joukkue[j].poistaKuski(poistokuski) ) {
                  onnistuikoPoisto = false;
                } 
              } while(onnistuikoPoisto);
              
              System.out.print("Anna lisättävän kuskin numero: ");
              boolean onnistuikoLisays = true;
              do {
                int lisayskuski = Integer.parseInt(s.next() );
                int valietappi = i+2;
                if(joukkue[j].lisaaKuski(kuski[lisayskuski-1], lisayskuski, valietappi) ) {
                  // poistutaan lisäysluupista
                  onnistuikoLisays = false;
                  // System.out.print("Lisättiin: ");
                  kuski[lisayskuski-1].tulostaKuski();
                }
              } while(onnistuikoLisays);
            } else {
              tulikoVaihto = false;
            } 
          } while(tulikoVaihto);
        } // pelaajien määrä
      }// etappien määrä
      
      // lopputulospisteet
      // luupataan joukkueiden läpi
      Random rnd = new Random();
      ArrayList<Integer> kokonaiskisa = new ArrayList<Integer>();
      int[] kokonaiskisaPinnat = {25,20,18,16,15,14,13,12,11,10,7,7,6,6,5,3,2,2,1,1};
      int r=0;
      System.out.println("Kokonaiskilpailun lopputulos: ");
      while (kokonaiskisa.size() < 20) { // kuinka monta kuskia arvotaan voittajaksi
        int random = rnd.nextInt(kuskienMaara)+1; 
        if (!kokonaiskisa.contains(random)) {
          kokonaiskisa.add(random);
          // tarkistetaan joukkueittain
          for(int j=0;j<pelaajaMaara;j++) {
            joukkue[j].kokonaiskisa(random, kokonaiskisaPinnat[r]);
          }
          r++;
        }
      }
      for(int i=0;i<20;i++){
        System.out.print("Kuski nro " + kokonaiskisa.get(i) + ": " + kokonaiskisaPinnat[i] + " pts. ");
      }
      System.out.println("");
    
      // kokonaiskisan pisteet
        // mäkikisan pisteet
      int[] pistekisaPinnat = {10,7,5,3,3,2,2,1,1,1};
      ArrayList<Integer> pistekisa = new ArrayList<Integer>();
      r=0;
      System.out.println("Pistekilpailun lopputulos: ");
      while (pistekisa.size() < 10) { // kuinka monta kuskia arvotaan voittajaksi
        int random = rnd.nextInt(kuskienMaara)+1; 
        if (!pistekisa.contains(random)) {
          pistekisa.add(random);
          // tarkistetaan joukkueittain
          for(int j=0;j<pelaajaMaara;j++) {
            joukkue[j].kokonaiskisa(random, pistekisaPinnat[r]);
          }
          r++;
        }
      }
      for(int i=0;i<10;i++){
        System.out.print("Kuski nro " + pistekisa.get(i) + ": " + pistekisaPinnat[i] + " pts. ");
      }
      System.out.println("");
      
      ArrayList<Integer> makikisa = new ArrayList<Integer>();
        // nuorten kisan pisteet        
      r=0;
      System.out.println("Mäkikilpailun lopputulos: ");
      while (makikisa.size() < 10) { // kuinka monta kuskia arvotaan voittajaksi
        int random = rnd.nextInt(kuskienMaara)+1; 
        if (!makikisa.contains(random)) {
          makikisa.add(random);
          // tarkistetaan joukkueittain
          for(int j=0;j<pelaajaMaara;j++) {
            joukkue[j].kokonaiskisa(random, pistekisaPinnat[r]);
          }
          r++;
        }
      }
      for(int i=0;i<10;i++){
        System.out.print("Kuski nro " + makikisa.get(i) + ": " + pistekisaPinnat[i] + " pts. ");
      }
      System.out.println("");
      
      
    //}
      for(int i=0;i<pelaajaMaara;i++) {
        // jos kuskin etappi on 1, tulee bonus
        joukkue[i].alkuperainenKuski();
      }
      
      System.out.println("---------------- Tulokset joukkuettain: --------------------");
      for(int i=0;i<pelaajaMaara;i++) {
        joukkue[i].tulostaJoukkue();
      }
      
    }
}
   
    class Kuski {
      private String nimi;
      private int numero;
      private String tiimi;
      private int hinta;
      private int pisteet;
      private String paita;
      
      public Kuski (int nro, String n, String t, int h){
        nimi = n;
        numero = nro;
        tiimi = t;
        hinta = h;
        pisteet = 0;
        paita = "-";
      }
      
      public int annaNumero(){
        return numero;
      }
      
      public String annaNimi() {
        return nimi;
      }
      
      public String annaTiimi() {
        return tiimi;
      }
      
      public String annaPaita() {
        return paita;
      }
      
      public void asetaPaita(String p) {
        paita = p;
      }
      
      public void tulostaKuski() {
        System.out.println(numero + ": " + nimi + ", " + tiimi + " " + hinta + "€, " + pisteet + " pts");
      }
      
      public void lisaaPisteet(int p) {
        pisteet += p;
      }
      
      public int annaPisteet() {
        return pisteet;
      }
      
      public int annaHinta() {
        return hinta;
      }
    }
    
    class Joukkue {
      private String nimi;
      private int pisteet = 0;
      private int hinta = 0;
      final int maksimihinta=10000;
      final int maksimivaihdot=8;
      private int vaihdot=0;
      final int kuskimaara = 10;
      Kuski[] kuski = new Kuski[kuskimaara];
      int[][] liittymisEtappi = new int [kuskimaara][2]; // ekassa kuskin nro, toisessa etappi
      final int etappienMaara = 3;
      
      public Joukkue (String n) {
        nimi = n;
        for(int i=0;i<kuskimaara;i++) {
          liittymisEtappi[i][0] = 0;
          liittymisEtappi[i][1] = 0;
        }
      }
      
      public void tulostaJoukkue() {
        System.out.println("Joukkue " + nimi + ": " + pisteet + " pts, Hinta: " + hinta + ", vaihtoja jäljellä: " + (maksimivaihdot-vaihdot));
        for(int k=0;k<kuskimaara;k++) {
          kuski[k].tulostaKuski();
        }
      }
      
      public String annaNimi() {
        return nimi;
      }
      
      public void kokonaiskisa(int kuskinNro, int pinnat) {
        // tarkista onko kuski joukkueessa, kerro sisääntuloetapilla
        for(int i=0;i<kuskimaara;i++) {
          if(liittymisEtappi[i][0] == kuskinNro) {
            int valipinnat = pinnat*(etappienMaara-liittymisEtappi[i][1]);
            pisteet += valipinnat;
            System.out.println("Kuski nro " + kuskinNro + " toi joukkueelle " + nimi + " " + valipinnat + " pts");
          }
        }
      }
      
      public void lisaaTiimiPisteet(String tiimi, boolean ekaPelaaja) {
        // loopataan joukkueen kuskit läpi
        // annetaan pisteet, jos kuskin tiimi oli argumentti
        for(int i=0;i<kuskimaara;i++){
          if(kuski[i].annaTiimi() == tiimi) {
            pisteet += 5;
            //System.out.println(nimi + ": 5 tiimipistettä kuskilta " + kuski[i].annaNimi() + ", " + kuski[i].annaTiimi() );
            if (ekaPelaaja) {
              // kuskille henkilökohtaiset pisteet
              kuski[i].lisaaPisteet(5);
            }
          }
        }
      }
      
      public void alkuperainenKuski() {
        // luupataan etappilista läpi
        for(int i=0;i<kuskimaara;i++) {
          // jos etappi 1, tulee bonus
          if (liittymisEtappi[i][1] == 1) {
            // hae pelaajan pisteet ja anna bonus
            double bonus = kuski[i].annaPisteet();
            bonus = Math.round(bonus*1.2);
            pisteet += bonus;
            System.out.println("Sait kuskista " + kuski[i].annaNimi() + " alkuperäisen kuskin bonusta " + bonus + " pts");
          }
        }
      }
      
      public void lisaaIrtiottoPisteet(int voittaja, boolean ekaPelaaja) {
        // jos voittaja on sama kuin tiimi kuskin numero, anna pisteet
        for(int i=0;i<kuskimaara;i++){
          if(kuski[i].annaNumero() == voittaja) {
            pisteet += 10;
            //System.out.println("10 irtiottopistettä kuskilta " + kuski[i].annaNimi() );
            if(ekaPelaaja) {
              kuski[i].lisaaPisteet(10);
            }
          }
        }
      }
      
      public void lisaaPaitaPisteet(ArrayList<Integer> paidat, boolean ekaPelaaja) {
        
      }
      
      public int annaPinnat() {
        return pisteet;
      }
      
      public void nollaaVaihdot(){
        // tällainen sitä varten, ettei ensimmäisellä kierroksella mene turhaan vaihtoja
        vaihdot = 0;
      }
      
      public int laskeKierrosPisteet(ArrayList<Integer> voittajat, boolean ekaPelaaja) {
        // täältä tulee argumenttina kierroksen kärjen numerot...
        // katsotaan, onko numero joukkueessa
        // annetaan vastaava määrä pinnoja
        int[] etapinPinnat = {100,70,50,35,30,25,20,16,13,10,7,5,3,2,1};
        int pinnat = 0;
        // jos argumenttina tulleessa listassa ollut kuski on joukkueen tiimissä, annetaan joukkueelle vastaavat pisteet
        // myös kuskille lisätään pisteet
        // luupataan joukkueen kuskit läpi
        for(int i=0;i<kuskimaara;i++){
          for(int j=0;j<etapinPinnat.length;j++){
            if(voittajat.get(j) == liittymisEtappi[i][0] ) {
              pinnat += etapinPinnat[j];
              //System.out.println("Kuskilta nro " + liittymisEtappi[i][0] + " tuli etapilta " + etapinPinnat[j] + " pts");
              /*
               * 10
               * 
                // kuskille henkilökohtaiset pisteet
                // looppaa läpi kuskit ja anna pisteet
                for(int k=0;k<kuskimaara;k++) {
                  if (kuski[k].annaNumero() == voittajat.get(j) ) {
                    kuski[k].lisaaPisteet(etapinPinnat[j] );
                  }
                }
              }
              */
            }
          }
        }
        pisteet += pinnat;
        return pinnat;
      }
      
      public void alustaJoukkue() {
        hinta = 0;
        for(int i=0;i<kuskimaara;i++){
          kuski[i] = null;
          liittymisEtappi[i][0] = 0;
          liittymisEtappi[i][1] = 0;
        }
        System.out.println("Joukkue nollattu");
      }
      
      public boolean lisaaKuski(Kuski k, int nro, int etappi){
        // kuskin numero tulee argumenttina 
        // lisaa kuskin, palauttaa true, jos onnistuu
        // tarkistaa vain, onko vaihtoja käytettävissä eikä ole liian kallis
        boolean saakoVaihtaa = false;
        if(vaihdot<8) {
          // määrä ok
          saakoVaihtaa = true;
        }
        int kh = k.annaHinta();
        if(kh+hinta>maksimihinta) {
          saakoVaihtaa = false;
          return false;
        }
        // tarkastetaan, onko tuplakuskia
        for(int i=0;i<kuskimaara;i++){
          if(liittymisEtappi[i][0] == nro) {
            saakoVaihtaa = false;
            System.out.println("Kuski on jo joukkueessa, valitse toinen kuski.");
            return false;
          }
        }
        
        if(saakoVaihtaa){
          // varsinainen kuskin lisäys
          // poistoissa on merkitty tyhjä paikka nollaksi, joten luupataan ja laitetaan nollan päälle
          for(int i=0;i<kuskimaara;i++){
            if(0 == liittymisEtappi[i][1]) {
              // liittymisetappi nolla, kuski tähän
              liittymisEtappi[i][0] = nro;
              liittymisEtappi[i][1] = etappi;
              kuski[i] = k;
              System.out.println(k.annaNimi() + " lisätty joukkueeseen.");
              return true;
            }
          }
        // jos tultiin tänne, kävi köpelösti
          System.out.print("Lisäys ei onnistu. Valitse uusiksi: ");
          return false;
        }
        System.out.print("Lisäys ei onnistu. Valitse uusiksi: ");
        return false;
      }
      
      public boolean poistaKuski(int nro){
        // poistaa kuskin, palauttaa true jos onnistuu
        // laitetaan poistettava kuski nollaksi, jotta lisäyksessä tiedetään, mihin paikkaan lisätään
        // onko samalla numerolla olevaa kuskia, luupataan
        // tänne pitää laittaa niiden maksimivaihtojen tutkinta
        if(vaihdot = maksimivaihdot) {
          return false;
        }
        boolean saakoPoistaa = false;
        for(int i=0;i<kuskimaara;i++){
          if(nro == liittymisEtappi[i][0]){
            liittymisEtappi[i][1] = 0;
            // kuski[] pitäisi jotenkin löytää jatkossa, pitäisikö se laittaa liittymisetappiin jotenkin eri tavalla
            // ehkä laitetaankin liittymisetappi nollaksi!
            saakoPoistaa = true;
            System.out.println("Kuski poistettu joukkueesta.");
            vaihdot += 1;
            return true;
          }
        }
        System.out.print("Poisto ei onnistu. Valitse uusiksi: ");
        return false;
      }
      
      
    }
    
    
    