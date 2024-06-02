#include <iostream>
#include <sqlite3.h>

using namespace std;

int main() {
    sqlite3* db;
    char* errMsg = 0;

    // Abrir la base de datos o crearla si no existe
    int rc = sqlite3_open("empleados.db", &db);

    if (rc) {
        cerr << "Error al abrir la base de datos: " << sqlite3_errmsg(db) << endl;
        return 1;
    } else {
        cout << "Base de datos abierta correctamente" << endl;
    }

    // Crear la tabla empleados
    const char* sql = "CREATE TABLE empleados ("
                      "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                      "nombre TEXT NOT NULL,"
                      "apellido TEXT NOT NULL,"
                      "fecha_nacimiento DATE,"
                      "email TEXT UNIQUE,"
                      "salario REAL,"
                      "fecha_contratacion DATE DEFAULT CURRENT_DATE"
                      ");";

    rc = sqlite3_exec(db, sql, 0, 0, &errMsg);

    if (rc != SQLITE_OK) {
        cerr << "Error al crear la tabla: " << errMsg << endl;
        sqlite3_free(errMsg);
    } else {
        cout << "Tabla creada correctamente" << endl;
    }

    // Cerrar la conexión a la base de datos
    sqlite3_close(db);

    return 0;
}
