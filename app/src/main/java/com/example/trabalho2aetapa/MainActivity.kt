package com.example.trabalho2aetapa

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var dbHelper: StudentDatabaseHelper
    private lateinit var listView: ListView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var streetEditText: EditText
    private lateinit var numberEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var addButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var mapButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var clearButton: Button

    private var selectedStudentId: Int? = null
    private var googleMap: GoogleMap? = null
    private var selectedAddress: String? = null



    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = StudentDatabaseHelper(this)
        listView = findViewById(R.id.listView)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        streetEditText = findViewById(R.id.streetEditText)
        numberEditText = findViewById(R.id.numberEditText)
        cityEditText = findViewById(R.id.cityEditText)
        stateEditText = findViewById(R.id.stateEditText)
        countryEditText = findViewById(R.id.countryEditText)
        addButton = findViewById(R.id.addButton)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)
        mapButton = findViewById(R.id.mapButton)
        clearButton = findViewById(R.id.clearButton)

        Toast.makeText(this, "Dupla: Thaian Ramalho e Samir Ribeiro", Toast.LENGTH_LONG).show()

        listView.setOnItemClickListener { _, _, position, _ ->
            val cursor = listView.adapter.getItem(position) as Cursor
            selectedStudentId = cursor.getInt(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_ID))
            nameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_NAME)))
            emailEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_EMAIL)))
            streetEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_STREET)))
            numberEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_NUMBER)))
            cityEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_CITY)))
            stateEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_STATE)))
            countryEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_COUNTRY)))
        }

        clearButton.setOnClickListener {
            clearFields()
        }

        addButton.setOnClickListener {
            addStudent()
        }

        updateButton.setOnClickListener {
            updateStudent()
        }

        deleteButton.setOnClickListener {
            deleteStudent()
        }

        mapButton.setOnClickListener {
            openMapDialog()
        }



        checkLocationPermission()
        loadStudents()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    private fun openMapDialog() {
        selectedAddress = buildAddress()

        if (selectedAddress.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, selecione um aluno com um endereço válido", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogFragment = MapDialogFragment(selectedAddress!!)
        dialogFragment.show(supportFragmentManager, "MapDialogFragment")
    }




    private fun buildAddress(): String {
        val street = findViewById<EditText>(R.id.streetEditText).text.toString()
        val number = findViewById<EditText>(R.id.numberEditText).text.toString()
        val city = findViewById<EditText>(R.id.cityEditText).text.toString()
        val state = findViewById<EditText>(R.id.stateEditText).text.toString()
        val country = findViewById<EditText>(R.id.countryEditText).text.toString()
        return "$street, $number, $city, $state, $country"
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }
    private fun addStudent() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val street = streetEditText.text.toString()
        val number = numberEditText.text.toString()
        val city = cityEditText.text.toString()
        val state = stateEditText.text.toString()
        val country = countryEditText.text.toString()

        if (name.isEmpty() || email.isEmpty() || street.isEmpty() || number.isEmpty() || city.isEmpty() || state.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val values = ContentValues().apply {
            put(StudentDatabaseHelper.COLUMN_NAME, name)
            put(StudentDatabaseHelper.COLUMN_EMAIL, email)
            put(StudentDatabaseHelper.COLUMN_STREET, street)
            put(StudentDatabaseHelper.COLUMN_NUMBER, number)
            put(StudentDatabaseHelper.COLUMN_CITY, city)
            put(StudentDatabaseHelper.COLUMN_STATE, state)
            put(StudentDatabaseHelper.COLUMN_COUNTRY, country)
        }

        dbHelper.writableDatabase.insert(StudentDatabaseHelper.TABLE_NAME, null, values)
        loadStudents()
        clearFields()
    }

    private fun updateStudent() {
        selectedStudentId?.let { id ->
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val street = streetEditText.text.toString()
            val number = numberEditText.text.toString()
            val city = cityEditText.text.toString()
            val state = stateEditText.text.toString()
            val country = countryEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || street.isEmpty() || number.isEmpty() || city.isEmpty() || state.isEmpty() || country.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return
            }

            val values = ContentValues().apply {
                put(StudentDatabaseHelper.COLUMN_NAME, name)
                put(StudentDatabaseHelper.COLUMN_EMAIL, email)
                put(StudentDatabaseHelper.COLUMN_STREET, street)
                put(StudentDatabaseHelper.COLUMN_NUMBER, number)
                put(StudentDatabaseHelper.COLUMN_CITY, city)
                put(StudentDatabaseHelper.COLUMN_STATE, state)
                put(StudentDatabaseHelper.COLUMN_COUNTRY, country)
            }

            dbHelper.writableDatabase.update(StudentDatabaseHelper.TABLE_NAME, values, "${StudentDatabaseHelper.COLUMN_ID}=?", arrayOf(id.toString()))
            loadStudents()
            clearFields()
        }
    }

    private fun deleteStudent() {
        selectedStudentId?.let { id ->
            dbHelper.writableDatabase.delete(StudentDatabaseHelper.TABLE_NAME, "${StudentDatabaseHelper.COLUMN_ID}=?", arrayOf(id.toString()))
            loadStudents()
            clearFields()
        }
    }

    private fun loadStudents() {
        val cursor = dbHelper.readableDatabase.query(
            StudentDatabaseHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val adapter = SimpleCursorAdapter(
            this,
            android.R.layout.simple_list_item_2,
            cursor,
            arrayOf(StudentDatabaseHelper.COLUMN_NAME, StudentDatabaseHelper.COLUMN_EMAIL),
            intArrayOf(android.R.id.text1, android.R.id.text2),
            0
        )

        listView.adapter = adapter
    }

    private fun clearFields() {
        nameEditText.text.clear()
        emailEditText.text.clear()
        streetEditText.text.clear()
        numberEditText.text.clear()
        cityEditText.text.clear()
        stateEditText.text.clear()
        countryEditText.text.clear()
        selectedStudentId = null
    }


    private fun openMap() {
        selectedStudentId?.let { id ->
            val cursor = dbHelper.readableDatabase.query(
                StudentDatabaseHelper.TABLE_NAME,
                null,
                "${StudentDatabaseHelper.COLUMN_ID}=?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                val address = "${cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_STREET))}, " +
                        "${cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_NUMBER))}, " +
                        "${cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_CITY))}, " +
                        "${cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_STATE))}, " +
                        "${cursor.getString(cursor.getColumnIndexOrThrow(StudentDatabaseHelper.COLUMN_COUNTRY))}"

                val latLng = LatLng(0.0, 0.0)
                googleMap?.addMarker(MarkerOptions().position(latLng).title(address))
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    val latLng = LatLng(location.latitude, location.longitude)
                    googleMap?.addMarker(MarkerOptions().position(latLng).title(address))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else {
                    Toast.makeText(this, "Não foi possível encontrar a localização", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = ProgressBar.VISIBLE
                thread {
                    Thread.sleep(2000)
                    runOnUiThread {
                        progressBar.visibility = ProgressBar.GONE
                    }
                }
            }
        }
    }

    class StudentDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            val createTableStatement = """
                CREATE TABLE $TABLE_NAME (
                    _id INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_NAME TEXT,
                    $COLUMN_EMAIL TEXT,
                    $COLUMN_STREET TEXT,
                    $COLUMN_NUMBER TEXT,
                    $COLUMN_CITY TEXT,
                    $COLUMN_STATE TEXT,
                    $COLUMN_COUNTRY TEXT
                )
            """
            db.execSQL(createTableStatement)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }

        companion object {
            const val DATABASE_NAME = "students.db"
            const val DATABASE_VERSION = 3
            const val TABLE_NAME = "students"
            const val COLUMN_ID = "_id"
            const val COLUMN_NAME = "name"
            const val COLUMN_EMAIL = "email"
            const val COLUMN_STREET = "street"
            const val COLUMN_NUMBER = "number"
            const val COLUMN_CITY = "city"
            const val COLUMN_STATE = "state"
            const val COLUMN_COUNTRY = "country"
        }
    }
}
