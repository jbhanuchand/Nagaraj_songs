package god.bhagwan.bhajans.mantra;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PresongsDTO {

    //when app is opened for the very first time, creates a db with songs in it,
    //new downloaded songs are also added to this db.
    //next time when app all the songs in db are fetched, either the initial loaded ones or initial loaded ones+downloaded songs.

    DatabaseHelper dbh;
    String mysongs = "[{'name':'जय अंबे गौरी\nJay Ambe Gouri','lyrics':'','groupName':'मा दुर्गा भजन\nMata Bhajans','path':'jay_ambe_gauri','imageId':'jmd5'},{'name':'चलो बुलवा आया है\nChalo Bulawa Aaya Hai','lyrics':'','groupName':'मा दुर्गा भजन\nMata Bhajans','path':'chalo_bulawa_aaya_hai','imageId':'jmd8'},{'name':'भक्तन को दर्शन दे गई देवी\nBhakton Ko Darshan De Gayi Devi','lyrics':'','groupName':'मा दुर्गा भजन\nMata Bhajans','path':'bhakton_ko_darshan_de_gayi_devi','imageId':'jmd1'},{'name':'नंगे नंगे पाव चल आ गया\nNange Nange Paon Chal Aa Gaya','lyrics':'','groupName':'मा दुर्गा भजन\nMata Bhajans','path':'nange_nange_paon_chal_aa_gaya','imageId':'jmd4'},{'name':'तूने मुझे बुलाया शेरावालिये\nTuni Mujhe Bulaya Sherawliye','lyrics':'','groupName':'मा दुर्गा भजन\nMata Bhajans','path':'tune_mujhe_bulaya_sherawaliyein','imageId':'jmd7'},{'name':'महामूर्तियुंजय मंत्र\nMahamrityunjaya Mantra','lyrics':'','groupName':'भोलेनाथ\nShiv Bhajans','path':'mahamrityunjaya','imageId':'shiv6'},{'name':'ॐ जया शिव ओमकारा\nOm Jaya Shiv Omkara','lyrics':'','groupName':'भोलेनाथ\nShiv Bhajans','path':'om_jai_shiv_omkara','imageId':'shiv1'},{'name':'जय गणपति गजानन\nJai Ganapati Gajanana','lyrics':'','groupName':'गणपति आरती \nGanesh Bhajans','path':'jay_ganapati_gajanan','imageId':'ganesh1'},{'name':'जय गणेश देव\nJai Ganesh Deva','lyrics':'','groupName':'गणपति आरती \nGanesh Bhajans','path':'jay_ganesh_deva','imageId':'ganesh2'},{'name':'सुख कर्ता दूख हर्था\nSukhkarta Dukhharta','lyrics':'','groupName':'गणपति आरती \nGanesh Bhajans','path':'sukhkarta_dukhharta','imageId':'ganesh3'},{'name':'वक्रतुंड महाकाया\nVakratunda Mahakaya','lyrics':'','groupName':'गणपति आरती \nGanesh Bhajans','path':'vakratunda_mahakaya','imageId':'ganesh4'},{'name':'हनुमान चालीसा\nHanuman Chalisa','lyrics':'','groupName':'हनुमान चालीसा\nHanuman Chalisa','path':'shree_hanuman_chalisa','imageId':'hanuman_chalisa'},{'name':'हरे कृष्ण, हरे राम\nHare Krisha, Hare Rama','lyrics':'','groupName':'किशन कन्हैया\nKrishna Bhajans','path':'hare_krishna','imageId':'hare_krishna'},{'name':'माया यशोदा\nMaiyaa Yashoda','lyrics':'','groupName':'किशन कन्हैया\nKrishna Bhajans','path':'maiyaa_yashoda','imageId':'maiyaa_yashoda'},{'name':'जय राधा माधव\nJai Radha Madhav','lyrics':'','groupName':'किशन कन्हैया\nKrishna Bhajans','path':'jai_radha_madhav','imageId':'jai_radha_madhav'},{'name':'यशो माता नंदन\nYashomati Nandan','lyrics':'','groupName':'किशन कन्हैया\nKrishna Bhajans','path':'yashomati_nandan_','imageId':'yashomati_nandan_'},{'name':'गायत्री मंत्र\nGayatri Mantra','lyrics':'','groupName':'गायत्री मंत्र\nGayatri Mantra','path':'gayatri_mantra','imageId':'gayatri_mantra'},{'name':'साईं राम साईं श्याम\nSai Ram Sai Shyam','lyrics':'','groupName':'साई राम\nShirdi Sai Baba','path':'sai_ram_sai_shyam','imageId':'sai1'},{'name':'Shirdi Wale Saibaba','lyrics':'','groupName':'साई राम\nShirdi Sai Baba','path':'shridi_wale_saibaba','imageId':'sai2'},{'name':'कही राम बांके\nKabhi Ram Banake','lyrics':'','groupName':'श्री राम भजन\nLord Ram Songs','path':'kabhi_ram_banake','imageId':'ram6'},{'name':'रघुपति राघव राजा राम\nRaghupati Raghav Raja Ram','lyrics':'','groupName':'श्री राम भजन\nLord Ram Songs','path':'raghupati_raghav_raja_ram','imageId':'ram9'},{'name':'विष्णु साहसरामम\nVishnu Sahasranamam','lyrics':'','groupName':'श्री विष्णु साहसरामम\nSahasranama','path':'vishnu_sahasranamam','imageId':'vishnu1'}]";
    JSONObject songs;
    SQLiteDatabase mydatabase;

    boolean insertToDB(Context context) {
        loadJson(context);
        return true;
    }

    void loadJson(Context context) {
        try {
            JSONArray jr = new JSONArray(mysongs);
            dbh = new DatabaseHelper(context);
            for (int i = 0; i < jr.length(); i++) {
                songs = (JSONObject) jr.getJSONObject(i);
                String name = songs.getString("name");
                String lyrics = songs.getString("lyrics");
                String groupName = songs.getString("groupName");
                String path = songs.getString("path");
                String imageId = songs.getString("imageId");
                dbh.insertData(name, lyrics, groupName, "res", path, imageId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Cursor displayRaw(Context context) {
        dbh = new DatabaseHelper(context);
        return dbh.myRawQuery();
    }
}
