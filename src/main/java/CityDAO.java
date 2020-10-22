import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class CityDAO implements DAO<City>{
  final Connection connection;

  public CityDAO(Connection connection) {
    this.connection = connection;
  }

  @Override
  public City get(int id) {
    try (Statement stmt = connection.createStatement()) {
      try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM city WHERE id = " + id)) {
        while (rs.next()) {
          return new City (rs.getInt("id"), rs.getString("name"));
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    throw new IllegalStateException("Record with id " + id + "not found");
  }

  @Override
  public List<City> getAll() {
    final List<City> result = new ArrayList<>();
    try (Statement stmt = connection.createStatement()) {
      try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM city")) {
        while (rs.next()) {
          result.add(new City (rs.getInt("id"), rs.getString("name")));
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return result;
  }

  @Override
  public void save(City entity) {
    try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO city(id,name) VALUES(?,?)")) {
      preparedStatement.setInt(1, entity.getId());
      preparedStatement.setString(2, entity.getName());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void update(City entity) {
    try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE city SET name = ? WHERE id = ?")) {
      int cnt = 1;
      preparedStatement.setString(cnt++, entity.getName());
      preparedStatement.setInt(cnt++, entity.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void delete(City entity) {
    try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM city WHERE id = ?")) {
      preparedStatement.setInt(1, entity.getId());
      if (preparedStatement.executeUpdate() == 0) {
        throw new IllegalStateException("Record with id = " + entity.getId() + " not found");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
