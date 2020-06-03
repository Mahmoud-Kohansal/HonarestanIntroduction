package ir.medu.khn.highschoolmajors;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SchoolsActivity extends AppCompatActivity implements SchoolFilterDialog.SchoolFilterDialogListener{

    RecyclerView mSchools_RecyclerView;
    LinearLayoutManager mSchoolsRcVw_LayoutManager;
    SchoolsRcVwAdapter mSchoolsRcVw_Adapter;
    ArrayList<SchoolInfoItem> mSchoolInfoItems_List;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schools);
        setTitle(getString(R.string.title_school_activity));
        //Define view objects
        defineObjects();
        //Read fields json and Fill adapter
        mSchoolInfoItems_List = makeSchoolsListFromJsonFile();
        buildRecyclerView();
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater schoolsMenuInflator = getMenuInflater();
        schoolsMenuInflator.inflate(R.menu.schools_menu,menu);

        MenuItem search_MnuItem = menu.findItem(R.id.search_SchoolsMnuItm);
        SearchView searchView = (SearchView) search_MnuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        MenuItem filter_MnuItem = menu.findItem(R.id.filter_SchoolsMnuItm);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                mSchoolsRcVw_Adapter.getFilter().filter(queryText);
                return false;
            }
        });


        filter_MnuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.filter_SchoolsMnuItm)
                {
                    openSchoolFilterDialog();
                }
                return false;
            }
        });
        return true;
    }

    private void defineObjects() {
        mSchools_RecyclerView = findViewById(R.id.schoolsItems_RcVw);

    }

    private void buildRecyclerView()
    {        
        mSchools_RecyclerView.setHasFixedSize(true);
        mSchoolsRcVw_LayoutManager = new LinearLayoutManager(this);
        mSchoolsRcVw_Adapter = new SchoolsRcVwAdapter(mSchoolInfoItems_List);
        mSchools_RecyclerView.setLayoutManager(mSchoolsRcVw_LayoutManager);
        mSchools_RecyclerView.setAdapter(mSchoolsRcVw_Adapter);
    }

    private ArrayList<SchoolInfoItem> makeSchoolsListFromJsonFile()
    {
        ArrayList<SchoolInfoItem> schoolInfoItems = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(readJSON("schools.json"));
            JSONArray schoolsArray = object.getJSONArray(getString(R.string.json_tag_school_schools));
            for (int i = 0; i < schoolsArray.length(); i++) {

                JSONObject jsonObject = schoolsArray.getJSONObject(i);
                String provinceName = jsonObject.getString(getString(R.string.json_tag_school_province));
                String schoolName = jsonObject.getString(getString(R.string.json_tag_school_name));
                String schoolGender = jsonObject.getString(getString(R.string.json_tag_school_gender));
                String schoolFields = jsonObject.getString(getString(R.string.json_tag_school_fields));
                String schoolAddress = jsonObject.getString(getString(R.string.json_tag_school_address));

                SchoolInfoItem schoolInfoItem = new SchoolInfoItem();
                schoolInfoItem.setProvinceName(provinceName);
                schoolInfoItem.setSchoolName(schoolName);
                schoolInfoItem.setFields(schoolFields);
                schoolInfoItem.setAddress(schoolAddress);
                schoolInfoItem.setGender(schoolGender);

                if(schoolGender.equals(getString(R.string.json_school_male_gender)))
                {
                    schoolInfoItem.setGenderImgSource(R.drawable.male);
                }
                else
                {
                    schoolInfoItem.setGenderImgSource(R.drawable.female);
                }
                schoolInfoItems.add(schoolInfoItem);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return schoolInfoItems;
    }

    private void openSchoolFilterDialog()
    {
        SchoolFilterDialog schoolFilterDialog = new SchoolFilterDialog();
        schoolFilterDialog.show(getSupportFragmentManager(),"School Filter Dialog");

    }
    @Override
    public void applyFilterFields(String province_FilterField, String field_FilterField, String gender_FilterField) throws UnsupportedEncodingException {
        //Toast.makeText(getApplicationContext(), province_FilterField + " " + field_FilterField + " " + gender_FilterField,Toast.LENGTH_LONG).show();

        ArrayList<SchoolInfoItem> provinceFilteredSchoolInfo_List = new ArrayList<>();
        ArrayList<SchoolInfoItem> fieldFilteredSchoolInfo_List = new ArrayList<>();
        ArrayList<SchoolInfoItem> genderFilteredSchoolInfo_List = new ArrayList<>();
        ArrayList<SchoolInfoItem> filteredSchoolInfo_List = new ArrayList<>(mSchoolInfoItems_List);
        //Filter Province
        if (province_FilterField != null && province_FilterField != "") {
            String province_FilterPattern = province_FilterField.toLowerCase().trim();
            for (SchoolInfoItem rcItem : filteredSchoolInfo_List) {
                if(rcItem.getProvinceName().toLowerCase().contains(province_FilterPattern))

                {
                    provinceFilteredSchoolInfo_List.add(rcItem);
                }
            }
            filteredSchoolInfo_List = new ArrayList<>(provinceFilteredSchoolInfo_List);
        }

        //Filter Field
        if (field_FilterField != null && field_FilterField != "") {
            String field_FilterPattern = field_FilterField.toLowerCase().trim();
            for (SchoolInfoItem rcItem : filteredSchoolInfo_List) {
                if (rcItem.getFields().toLowerCase().trim().contains(field_FilterPattern)) {
                    fieldFilteredSchoolInfo_List.add(rcItem);
                }

            }
            filteredSchoolInfo_List = new ArrayList<>(fieldFilteredSchoolInfo_List);
        }

        //Filter Gender
        if (gender_FilterField != null && gender_FilterField != "") {
            String gender_FilterPattern = gender_FilterField.toLowerCase().trim();
            for (SchoolInfoItem rcItem : filteredSchoolInfo_List) {
                if (rcItem.getGender().toLowerCase().trim().contains(gender_FilterPattern)) {
                    genderFilteredSchoolInfo_List.add(rcItem);
                }

            }
            filteredSchoolInfo_List = new ArrayList<>(genderFilteredSchoolInfo_List);
        }
        mSchoolsRcVw_Adapter.setFilteredList(filteredSchoolInfo_List);
    }
    
    public String readJSON(String fileNameInAssets) {
        String json = null;
        try {
            // Opening data.json file
            InputStream inputStream = getAssets().open(fileNameInAssets);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            inputStream.read(buffer);
            inputStream.close();
            // convert byte to string
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return json;
        }
        return json;
    }
}
