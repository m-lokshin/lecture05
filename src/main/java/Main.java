import org.flywaydb.core.Flyway;

import java.sql.*;

public final class Main {

  public static void main(String[] args) {
    final Flyway flyway = Flyway.configure()
        .dataSource("jdbc:postgresql://127.0.0.1:5438/l04", "postgres", "PgSQL12")
        .locations("db")
        .load();
    flyway.clean();
    flyway.migrate();

    try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5438/l04", "postgres", "PgSQL12"))
    {
      System.out.println("Connection Ok.");
      try (Statement stmt = connection.createStatement()) {
        int id = 1;
        String name = "Moscow";
        stmt.executeUpdate("INSERT INTO city(id,name) VALUES(" + id + ",'" + name + "')");
        id = 2;
        name = "Voronezh";
        stmt.executeUpdate("INSERT INTO city(id,name) VALUES(" + id + ",'" + name + "')");
      }
      try (Statement stmt = connection.createStatement()) {
        try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM city;")) {
          while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            System.out.println("ROW id:" + id + " name:" + name);
          }
          ResultSetMetaData rsmd = rs.getMetaData();
          int numcols = rsmd.getColumnCount();
          for (int i = 1; i <= numcols; i++) {
            System.out.print(rsmd.getColumnLabel(i) + " " );
          }
        }
      }
      DatabaseMetaData metaData = connection.getMetaData();
      ResultSet rs = metaData.getTypeInfo();
      while (rs.next()) {
        System.out.println(rs.getString("TYPE_NAME"));
      }

      try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO city(id,name) VALUES(?,?)")) {
        preparedStatement.setInt(1, 3);
        preparedStatement.setString(2, "Tambov");
        preparedStatement.executeUpdate();
      }

      final CityDAO cityDAO = new CityDAO(connection);

      cityDAO.save( new City(4, "Lipetsk"));

      for (City city : cityDAO.getAll() ){
        System.out.println("id : " + city.getId() + " name: " + city.getName());
      }
    }
    catch (SQLException e) {
      System.out.println("Connection failure.");
      e.printStackTrace();
    }


    System.out.println("Hello world.");

  }
}
