package cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.controler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Actividad;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Catalogo;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Sector;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.SituacionLaboral;
import cnb.pichincha.wposs.mivecino_pichincha.constructor.BDLogin.model.Usuario;
import cnb.pichincha.wposs.mivecino_pichincha.screens.Login;
import newpos.libpay.utils.PAYUtils;

public class ClsConexion extends SQLiteOpenHelper {

    public static Usuario persona;
    private Usuario person101 = Login.persona;
    private static final int DATABASE_VERSION = 3;
    private SQLiteDatabase sqLiteDatabase;
    private static final String DATABASE_NAME = "users_cnb.db";
    private final String ESTADOHABILIDAD = "HABILITADO";
    private final String TABLE_USER_NEW = "usuarios_new";
    private final String USUARIOADMIN = "1234567890";
    private final String PASSADMIN = "123456";
    private final String ROLADMIN = "ADMINISTRADOR";
    private final String ESTADOADMIN = "HABILITADO";
    private final int    INTENTOSADMIN = 0;
    private final String FECHAADMIN = PAYUtils.getLocalDate2();
    private final String HORAADMIN = PAYUtils.getLocalTime2();
    private final String FECHACIERREINICIAL = PAYUtils.getLocalDateCierre();
    private final String ESTADOCIERRE = "Abierto";

    private final String COLUMN_USER= "user";
    private final String COLUMN_USER_PASSWORD = "user_password";
    private final String COLUMN_USER_ROLE = "user_role";
    private final String COLUMN_USER_ESTADO = "user_estado";
    private final String COLUMN_USER_INTENTOS = "user_intento";
    private final String COLUMN_USER_FECHA = "user_fecha";
    private final String COLUMN_USER_HORA = "user_hora";
    private final String COLUMN_USER_FECHAULTIMOCIERRE = "user_fechacierre";
    private final String COLUMN_USER_ESTADOCIERRE = "user_estadocierre";

    final String CREATE_USER_TABLE_NEW = "create table " + TABLE_USER_NEW + "(" + COLUMN_USER +
            " text primary key not null, " + COLUMN_USER_PASSWORD + " text not null, " +
            COLUMN_USER_ROLE + " text not null, " + COLUMN_USER_ESTADO + " text not null, " + COLUMN_USER_INTENTOS +
            " integer not null, " + COLUMN_USER_FECHA + " text not null, " + COLUMN_USER_HORA + " text not null," +
            COLUMN_USER_FECHAULTIMOCIERRE + " text not null, " + COLUMN_USER_ESTADOCIERRE + " text not null);";

    final String INSERT_USER_ADMIN = ("insert into " + TABLE_USER_NEW +" values('"+USUARIOADMIN+"'," +
            "'"+PASSADMIN+"','"+ROLADMIN+"','"+ESTADOADMIN+"','"+INTENTOSADMIN+"','"+FECHAADMIN+"'" +
            ",'"+HORAADMIN+"','"+FECHACIERREINICIAL+"','"+ESTADOCIERRE+"');");

    private final String TABLE_CATALOGO_SECTOR_ECONOMICO = "tab_sector";
    private final String COLUMN_SECTOR_ID = "id_sector";
    private final String COLUMN_SECTOR_NAME = "name_sector";

    private final String TABLE_CATALOGO_ACTIVIDAD_ECONOMICA = "tab_actividad";
    private final String COLUMN_ACTIVIDAD_ID = "id_actividad";
    private final String COLUMN_ACTIVIDAD_NAME = "name_actividad";

    private final String CREATE_SECTOR = "create table " +
            TABLE_CATALOGO_SECTOR_ECONOMICO + "(" +
            COLUMN_SECTOR_ID + " text primary key not null, " +
            COLUMN_SECTOR_NAME +" text not null );";

    private final String CREATE_ACTIVIDAD = "create table " +
            TABLE_CATALOGO_ACTIVIDAD_ECONOMICA + "(" +
            COLUMN_ACTIVIDAD_ID + " text primary key not null, " +
            COLUMN_ACTIVIDAD_NAME + " text not null, " +
            COLUMN_SECTOR_ID + " text not null, " +
            "foreign key(" + COLUMN_SECTOR_ID +") references " +
            TABLE_CATALOGO_SECTOR_ECONOMICO + "(" + COLUMN_SECTOR_ID + "));";

    private final String TABLE_CATALOGO_SITUACION_LABORAL = "tab_laboral";
    private final String COLUMN_LABORAL_ID = "id_laboral";
    private final String COLUMN_LABORAL_NAME = "name_laboral";

    private final String CREATE_LABORAL = "create table " +
            TABLE_CATALOGO_SITUACION_LABORAL + "(" +
            COLUMN_LABORAL_ID + " text primary key not null, " +
            COLUMN_LABORAL_NAME +" text not null );";

    private final String TABLE_CATALOGO_PROVINCIA_INEN = "tab_provincias_inen";
    private final String TABLE_CATALOGO_PROVINCIA_ATM = "tab_provincias_atm";
    private final String COLUMN_PROVINCIA_ID = "id_provincia";
    private final String COLUMN_PROVINCIA_NAME = "name_provincia";

    private final String CREATE_PROVINCIAS_INEN = "create table " +
            TABLE_CATALOGO_PROVINCIA_INEN + "(" +
            COLUMN_PROVINCIA_ID + " text primary key not null, " +
            COLUMN_PROVINCIA_NAME +" text not null );";

    private final String CREATE_PROVINCIAS_ATM = "create table " +
            TABLE_CATALOGO_PROVINCIA_ATM + "(" +
            COLUMN_PROVINCIA_ID + " text primary key not null, " +
            COLUMN_PROVINCIA_NAME +" text not null );";

    public ClsConexion(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase wposs) {
        wposs.execSQL(CREATE_USER_TABLE_NEW);
        wposs.execSQL(INSERT_USER_ADMIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase wposs, int oldVersion, int newVersion) {
        if (oldVersion == 2) {
            wposs.execSQL("alter table " + TABLE_USER_NEW + " add column " + COLUMN_USER_ESTADOCIERRE
                    + " text not null default 'CERRADO'");
        }
    }

    /**
     *
     * @param user
     * @param pass
     * @param role
     */
    public void crearUsuario(String user, String pass, String role){
        sqLiteDatabase = this.getWritableDatabase();
        String mensaje = "";
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER, user);
        values.put(COLUMN_USER_PASSWORD, pass);
        values.put(COLUMN_USER_ROLE, role);
        values.put(COLUMN_USER_ESTADO, ESTADOHABILIDAD);
        values.put(COLUMN_USER_INTENTOS, INTENTOSADMIN);
        values.put(COLUMN_USER_FECHA, FECHAADMIN);
        values.put(COLUMN_USER_HORA, HORAADMIN);
        values.put(COLUMN_USER_FECHAULTIMOCIERRE, FECHACIERREINICIAL);
        values.put(COLUMN_USER_ESTADOCIERRE, ESTADOCIERRE);

        sqLiteDatabase.insert(TABLE_USER_NEW,null,values);

        try {
            sqLiteDatabase.insertOrThrow(TABLE_USER_NEW,null,values);
            sqLiteDatabase.close();
        }catch (SQLException e){
            mensaje = "No ingresado";
            System.out.print(mensaje);
        }
    }

    /**
     *
     * @param usuario
     * @return
     */
    public Usuario readUser(Usuario usuario) {
        sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_USER_NEW,
                new String[]{COLUMN_USER, COLUMN_USER_PASSWORD, COLUMN_USER_ROLE,COLUMN_USER_ESTADO,COLUMN_USER_INTENTOS,COLUMN_USER_ESTADOCIERRE},
                COLUMN_USER + "=?",
                new String[] {usuario.getUser()},
                null,null,null
        );

        if (cursor !=null && cursor.moveToFirst() && cursor.getCount()>0){
            Usuario person1 = new Usuario(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getString(5));
            persona = person1;
            persona.setUser(cursor.getString(0));
            persona.setPassword(cursor.getString(1));
            persona.setRole(cursor.getString(2));
            persona.setEstado(cursor.getString(3));
            persona.setIntento(cursor.getInt(4));
            persona.setEstadoCierre(cursor.getString(5));
            sqLiteDatabase.close();
            return person1;
        }
        sqLiteDatabase.close();
        return null;
    }

    public boolean validarUser(String usuario) {
        sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_USER_NEW,
                new String[]{COLUMN_USER, COLUMN_USER_PASSWORD, COLUMN_USER_ROLE},
                COLUMN_USER + "=?",
                new String[] {usuario},
                null,null,null
        );

        if (cursor != null && cursor.moveToFirst() && cursor.getCount()>0){
            return true;
        }
        sqLiteDatabase.close();
        return false;
    }

    public void updateUser(String usuario, String pass) {
        sqLiteDatabase = this.getWritableDatabase();
        String usuarioActualLogin = person101.getUser();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER, usuario);
        values.put(COLUMN_USER_PASSWORD, pass);
        sqLiteDatabase.update(TABLE_USER_NEW,values,"user='" + usuarioActualLogin + "'",null);
        sqLiteDatabase.close();
    }

    public ArrayList<Usuario> readUserList() {
        sqLiteDatabase = this.getWritableDatabase();
        Usuario usuario;
        ArrayList<Usuario> litaUsuario = new ArrayList<Usuario>();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_USER_NEW,null);

        while (cursor.moveToNext()){
            usuario = new Usuario();
            usuario.setUser(cursor.getString(0));
            usuario.setPassword(cursor.getString(1));
            usuario.setRole(cursor.getString(2));
            usuario.setEstado(cursor.getString(3));
            usuario.setIntento(cursor.getInt(4));
            usuario.setFecha(cursor.getString(5));
            usuario.setHora(cursor.getString(6));
            usuario.setFechaCierre(cursor.getString(7));
            usuario.setEstadoCierre(cursor.getString(8));
            litaUsuario.add(usuario);
        }
        sqLiteDatabase.close();
        return litaUsuario;
    }

    public void updatePass(String usuario, String pass, String estado) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER, usuario);
        values.put(COLUMN_USER_PASSWORD, pass);
        values.put(COLUMN_USER_ESTADO, estado);
        sqLiteDatabase.update(TABLE_USER_NEW,values,"user='" + usuario + "'",null);
        sqLiteDatabase.close();
    }

    public void deleteData(String usuario) {
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_USER_NEW, COLUMN_USER + "='"
                + usuario + "'", null);
        sqLiteDatabase.close();
    }


    public void updateFields(String busqueda,String column,String valor, String campo) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column, valor);
        sqLiteDatabase.update(TABLE_USER_NEW,values,campo + "='" + busqueda + "'",null);
        sqLiteDatabase.close();
    }

    public String selectFechaCierreUsuario(String busqueda, String campo, String filtro) {
        sqLiteDatabase = this.getWritableDatabase();
        String fecha = "error";
        Cursor cursor = sqLiteDatabase.rawQuery("select " +campo+ " from usuarios_new where " +filtro+ " = '" + busqueda + "'" ,null);
        if (cursor.moveToFirst()) {
            fecha = cursor.getString(0);
        }
        sqLiteDatabase.close();
        return fecha;
    }

    public void insertSectorEconomico(String id_sector, String name_sector){
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SECTOR_ID, id_sector);
        values.put(COLUMN_SECTOR_NAME, name_sector);
        try {
            sqLiteDatabase.insertOrThrow(TABLE_CATALOGO_SECTOR_ECONOMICO,null,values);
            sqLiteDatabase.close();
        }catch (SQLException e){
            e.getCause();
            sqLiteDatabase.close();
        }
    }

    public void insertActividadEconomica(String id_actividad, String name_actividad, String id_sector){
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTIVIDAD_ID, id_actividad);
        values.put(COLUMN_ACTIVIDAD_NAME, name_actividad);
        values.put(COLUMN_SECTOR_ID, id_sector);
        try {
            sqLiteDatabase.insertOrThrow(TABLE_CATALOGO_ACTIVIDAD_ECONOMICA,null,values);
            sqLiteDatabase.close();
        }catch (SQLException e){
            e.getCause();
            sqLiteDatabase.close();
        }
    }

    public void insertSituacionLaboral(String id_laboral, String name_laboral){
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LABORAL_ID, id_laboral);
        values.put(COLUMN_LABORAL_NAME, name_laboral);
        try {
            sqLiteDatabase.insertOrThrow(TABLE_CATALOGO_SITUACION_LABORAL,null,values);
            sqLiteDatabase.close();
        }catch (SQLException e){
            e.getCause();
            sqLiteDatabase.close();
        }
    }

    public void insertProvincias(String id_Item, String name_Item, boolean isATM){
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROVINCIA_ID, id_Item);
        values.put(COLUMN_PROVINCIA_NAME, name_Item);
        try {
            if (isATM){
                sqLiteDatabase.insertOrThrow(TABLE_CATALOGO_PROVINCIA_ATM,null,values);
            } else {
                sqLiteDatabase.insertOrThrow(TABLE_CATALOGO_PROVINCIA_INEN,null,values);
            }
            sqLiteDatabase.close();
        }catch (SQLException e){
            e.getCause();
            sqLiteDatabase.close();
        }
    }

    public void dropTableCatalogosCambioDia(){
        sqLiteDatabase = this.getWritableDatabase();
        try{
            sqLiteDatabase.execSQL("DROP TABLE " + TABLE_CATALOGO_ACTIVIDAD_ECONOMICA + ";");
        }catch (SQLException e){
            e.getCause();
        }
        try{
            sqLiteDatabase.execSQL("DROP TABLE " + TABLE_CATALOGO_SECTOR_ECONOMICO + ";");
        }catch (SQLException e){
            e.getCause();
        }
        try{
            sqLiteDatabase.execSQL("DROP TABLE " + TABLE_CATALOGO_SITUACION_LABORAL + ";");
        }catch (SQLException e){
            e.getCause();
        }
        try{
            sqLiteDatabase.execSQL("DROP TABLE " + TABLE_CATALOGO_PROVINCIA_INEN + ";");
        }catch (SQLException e){
            e.getCause();
        }
        try{
            sqLiteDatabase.execSQL("DROP TABLE " + TABLE_CATALOGO_PROVINCIA_ATM + ";");
        }catch (SQLException e){
            e.getCause();
        }

        sqLiteDatabase.execSQL(CREATE_SECTOR);
        sqLiteDatabase.execSQL(CREATE_ACTIVIDAD);
        sqLiteDatabase.execSQL(CREATE_LABORAL);
        sqLiteDatabase.execSQL(CREATE_PROVINCIAS_INEN);
        sqLiteDatabase.execSQL(CREATE_PROVINCIAS_ATM);

        sqLiteDatabase.close();
    }

    public ArrayList<Sector> selectAllSectores(){
        ArrayList<Sector> listaSectores = new ArrayList<>();
        sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_CATALOGO_SECTOR_ECONOMICO,null);

        while (cursor.moveToNext())
        {
            Sector sector = new Sector();
            sector.setIdSector(cursor.getString(0));
            sector.setSector(cursor.getString(1));
            listaSectores.add(sector);
        }
        sqLiteDatabase.close();
        return listaSectores;
    }

    public ArrayList<Actividad> selectAllActividades(String cod){
        System.out.println("Cod *********** "+cod);
        ArrayList<Actividad> listaActividades = new ArrayList<>();
        sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_CATALOGO_ACTIVIDAD_ECONOMICA +" WHERE "+ COLUMN_SECTOR_ID +" = '"+cod +"'",null);

        while (cursor.moveToNext()){
            Actividad actividad = new Actividad();
            actividad.setIdActividad(cursor.getString(0));
            actividad.setActividad(cursor.getString(1));
            actividad.setIdSector(cursor.getString(2));
            listaActividades.add(actividad);
        }
        sqLiteDatabase.close();
        return listaActividades;
    }

    public ArrayList<SituacionLaboral> selectAllSituacionLaboral(){
        ArrayList<SituacionLaboral> listaSituaciones = new ArrayList<>();
        sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_CATALOGO_SITUACION_LABORAL,null);

        while (cursor.moveToNext()){
            SituacionLaboral situacionLaboral = new SituacionLaboral();
            situacionLaboral.setId_laboral(cursor.getString(0));
            situacionLaboral.setName_laboral(cursor.getString(1));
            listaSituaciones.add(situacionLaboral);
        }
        sqLiteDatabase.close();
        return listaSituaciones;
    }

    public ArrayList<Catalogo> selectAllProvincias(boolean isATM){
        ArrayList<Catalogo> listaProvincias = new ArrayList<>();
        sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor;

        if (isATM){
            cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_CATALOGO_PROVINCIA_ATM,null);
        } else {
            cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_CATALOGO_PROVINCIA_INEN,null);
        }

        while (cursor.moveToNext()){
            Catalogo provincia = new Catalogo();
            provincia.setId_item(cursor.getString(0));
            provincia.setName_item(cursor.getString(1));
            listaProvincias.add(provincia);
        }
        sqLiteDatabase.close();
        return listaProvincias;
    }
}
