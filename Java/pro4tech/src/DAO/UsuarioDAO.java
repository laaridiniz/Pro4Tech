package DAO;

import Principal.Principal;
import modelo.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

 

public class UsuarioDAO {
    
    public void editarTelefone(Usuario usuario){
        String sql = "UPDATE usuario SET telefoneUsuario = ? WHERE usuarioId = ?;";
        
        try {
            
            try (PreparedStatement stmt = Principal.conexao.prepareStatement(sql)) {
                stmt.setString(1, usuario.getTelefoneUsuario());
                stmt.setInt(2, usuario.getUsuarioId());
                stmt.executeUpdate();
                
            }
        }catch (SQLException erro){
            throw new RuntimeException(erro);
        } 
    }
    
    public void editarSenha(Usuario usuario){
        String sql = "UPDATE usuario SET senhaUsuario = ? WHERE usuarioId = ?;";
        
        try {
            
            try (PreparedStatement stmt = Principal.conexao.prepareStatement(sql)) {
                stmt.setString(1, usuario.getSenha());
                stmt.setInt(2, usuario.getUsuarioId());
                stmt.executeUpdate();
                
            }
        }catch (SQLException erro){
            throw new RuntimeException(erro);
        } 
    }
    
    public List<Usuario> ListarUsuarios(){
       List<Usuario> usuarios = new ArrayList<>();
       String sqlSelecionarUsuario = "SELECT * FROM usuario;"; 
       
       try(PreparedStatement stmt = Principal.conexao.prepareStatement(sqlSelecionarUsuario)){
           
           ResultSet rs = stmt.executeQuery();
           
           while(rs.next()){
               
                Usuario usuario = new Usuario(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getInt(7),
                    rs.getString(8),
                    rs.getString(9),
                    rs.getInt(10)
                );
               
               usuarios.add(usuario);
               
            }
       }catch (SQLException erro){
           JOptionPane.showMessageDialog(null, erro);
       }
       
       return usuarios;
    }
    
    public List<Usuario> getUsuariosPorNome(String nome){
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario where nomeUsuario like '%" + nome + "%'";
        
        try(PreparedStatement stmt = Principal.conexao.prepareStatement(sql)){
            
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                
                Usuario usuario = new Usuario(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getInt(7),
                    rs.getString(8),
                    rs.getString(9),
                    rs.getInt(10)
                );
                
                usuarios.add(usuario);
            }
           
        }catch(SQLException e){
            e.printStackTrace();
        }

        return usuarios;
    }
    
    public boolean existeUsuario(String nomeUsuario){
        return ListarUsuarios().stream().anyMatch(r -> r.getNomeUsuario().equalsIgnoreCase(nomeUsuario));
    }
    
    public boolean existeLoginUsuario(String login){
        return ListarUsuarios().stream().anyMatch(r -> r.getLoginUsuario().equalsIgnoreCase(login));
    }
    
    public Usuario getUsuarioByUserName(String userName){
        if(!existeLoginUsuario(userName)){
            return null;
        }else{
            return ListarUsuarios().stream().filter(r-> r.getLoginUsuario().equalsIgnoreCase(userName)).findFirst().get();
        }
        
    }
   
    public HashMap<Usuario,Integer> getCountUsuario(int id){
        
        HashMap<Usuario,Integer> hash = new HashMap<>();
        try {
            PreparedStatement stmt = Principal.conexao.prepareStatement("select usuarioId, count(*) from mensagem where codProjeto = ? group by usuarioId;");
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                int usuarioId = rs.getInt("usuarioId");
                int count = rs.getInt("count(*)");

                Usuario usuario = Principal.daoUsuario.getUsuarioById(usuarioId);
                
                if(usuario != null){
                    hash.put(usuario, count);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }
    
    public boolean existeUsuario(int idUsuario){
        return ListarUsuarios().stream().anyMatch(r -> r.getUsuarioId() == idUsuario);
    }
    
    public Usuario getUsuarioById(int Id){
        
        try{
            PreparedStatement stmt = Principal.conexao.prepareStatement("select * from usuario where usuarioId = ?;");
            
            stmt.setInt(1, Id);
            
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                
                Usuario usuario = new Usuario(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getInt(7),
                    rs.getString(8),
                    rs.getString(9),
                    rs.getInt(10)
                );
                
                return usuario;
            }
            
        } catch (SQLException e){
            
        }
        return null;
    }
    
    public void excluirUsuario(Usuario usuario) {
        
        List<MensagemIndividual> mensagem = Principal.daoCadastro.ListarMensagensUsuarios(usuario.getUsuarioId());
        
        if (!mensagem.isEmpty()) {
            for(MensagemIndividual m : mensagem){
                Principal.daoCadastro.excluirMensagemIndividual(m.getCodMensagemIndividual());
            }
        }
        
        String sqlDeletar = "delete from usuario WHERE usuarioId =? and nomeUsuario = ?";

        try {
            PreparedStatement stmt = Principal.conexao.prepareStatement(sqlDeletar);
            stmt.setInt(1, usuario.getUsuarioId()); 
            stmt.setString(2, usuario.getNomeUsuario());
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ErroSql) {
            JOptionPane.showMessageDialog(null, "Este usuário está em projetos ativos");
        }        
    }
    
    public void editarUsuario(Usuario usuario) {

        String sql = "update usuario set nomeUsuario=?, empresaUsuario=?, funcaoUsuario=?, telefoneUsuario=?, emailUsuario=?, loginUsuario=?, senhaUsuario=?, perfilUsuario=? WHERE usuarioId=?;";

        try {

            PreparedStatement stmt = Principal.conexao.prepareStatement(sql);
            stmt.setString(1, usuario.getNomeUsuario());
            stmt.setString(2, usuario.getEmpresaUsuario());
            stmt.setString(3, usuario.getFuncaoUsuario());
            stmt.setString(4, usuario.getTelefoneUsuario());
            stmt.setString(5, usuario.getEmailUsuario());
            stmt.setString(6, usuario.getLoginUsuario());
            stmt.setString(7, usuario.getSenha());
            stmt.setInt(8, usuario.getPerfilUsuario());
            stmt.setInt(9, usuario.getUsuarioId());
            stmt.executeUpdate();

        } catch (SQLException erro) {
            throw new RuntimeException(erro);
        }

    }
    
}