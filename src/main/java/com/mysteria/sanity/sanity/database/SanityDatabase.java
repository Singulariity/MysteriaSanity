package com.mysteria.sanity.sanity.database;

import com.mysteria.sanity.SanityPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class SanityDatabase {

	public SanityDatabase() {
		if (SanityPlugin.getDatabase() != null) {
			throw new IllegalStateException();
		}
	}

	public void createData(@Nonnull UUID uuid) {
		try {
			PreparedStatement prst = SanityPlugin.getConnection()
					.prepareStatement("INSERT INTO sanity_data (Player) VALUES (?)");
			prst.setString(1, uuid.toString());
			prst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setInt(@Nonnull UUID uuid, @Nonnull Column column, int newValue) {
		try {
			PreparedStatement prst = SanityPlugin.getConnection()
					.prepareStatement("UPDATE sanity_data SET (" + column + ") = (?) WHERE Player = ?");
			prst.setInt(1, newValue);
			prst.setString(2, uuid.toString());
			prst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Nullable
	public Integer getInt(@Nonnull UUID uuid, @Nonnull Column column) {
		try {
			Statement st = SanityPlugin.getConnection().createStatement();
			String query = "SELECT " + column + " FROM sanity_data WHERE Player = '"+ uuid +"'";
			ResultSet resultSet = st.executeQuery(query);

			if (resultSet.next()) return resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean hasData(@Nonnull UUID uuid) {
		try {
			Statement st = SanityPlugin.getConnection().createStatement();
			String query = "SELECT 1 FROM sanity_data WHERE Player = '"+ uuid +"'";
			ResultSet resultSet = st.executeQuery(query);

			if (resultSet.next()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
