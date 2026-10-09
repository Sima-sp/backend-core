package br.com.back_end.simasp.usuario.service;

import br.com.back_end.simasp.exception.AlteracaoInvalidaException;
import br.com.back_end.simasp.exception.RecursoExistenteException;
import br.com.back_end.simasp.exception.RecursoNaoEncontradoException;
import br.com.back_end.simasp.usuario.dto.*;
import br.com.back_end.simasp.usuario.entity.Usuario;
import br.com.back_end.simasp.usuario.enums.TipoUsuarioEnum;
import br.com.back_end.simasp.usuario.mapper.UsuarioMapper;
import br.com.back_end.simasp.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<UsuarioResponse> buscarUsuariosProximos(BigDecimal longitude, BigDecimal latitude, Integer distanciaMetros, Pageable paginacao)
    {
        Page<Usuario> usuarios = repository.buscarUsuariosProximos(longitude, latitude, distanciaMetros, paginacao);
        return usuarios.map(mapper::paraUsuarioResponse);
    }

    @Transactional
    public UsuarioResponse cadastrarUsuario(CriarUsuarioRequest request)
    {
        String emailFormatado = request.email().toLowerCase(Locale.ROOT).trim();
        String telefoneFormatado = request.telefone().trim();
        verificarDuplicidade(emailFormatado, telefoneFormatado);

        Usuario usuario = mapper.paraEntidadeUsuario(request);

        usuario.setEmail(emailFormatado);
        usuario.setTelefone(telefoneFormatado);
        usuario.setSenhaHash(passwordEncoder.encode(request.senhaHash()));
        usuario.setTipoUsuario(TipoUsuarioEnum.CIDADAO);
        usuario.setAtivo(true);

        Usuario usuarioSalvo = repository.save(usuario);

        return mapper.paraUsuarioResponse(usuarioSalvo);
    }


    public UsuarioResponse buscarMeuPerfil(Long id)
    {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com id " + id + " não encontrado."));
        UsuarioResponse response = mapper.paraUsuarioResponse(usuario);
        if(!Boolean.TRUE.equals(response.ativo()))
        {
            throw new RecursoNaoEncontradoException("Usuário com id " + id + " foi desativado.");
        }
        return response;
    }

    @Transactional
    public void ativarUsuarioPorEmail(String email)
    {
        Usuario usuario = repository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT)).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com este e-mail não encontrado"));
        usuario.setAtivo(true);
    }

    @Transactional
    public void desativarUsuarioPorEmail(String email)
    {
        Usuario usuario = repository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT)).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com este e-mail não encontrado"));
        usuario.setAtivo(false);
    }

    @Transactional
    public void atualizarLocalizacao(AlterarLocalizacaoUsuarioRequest request, Long id)
    {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com este id não encontrado"));

        if(!Boolean.TRUE.equals(usuario.getAtivo()))
        {
            throw new RecursoNaoEncontradoException("Usuário com id " + id + " foi desativado.");
        }

        if(usuario.getLatitude().equals(request.latitude()) && usuario.getLongitude().equals(request.longitude()))
        {
            throw new AlteracaoInvalidaException("A nova localização é a mesma que a anterior.");
        }


        usuario.setLatitude(request.latitude());
        usuario.setLongitude(request.longitude());

    }

    @Transactional
    public void atualizarPermissaoAlerta(AlterarPermissaoAlertaRequest request, Long id)
    {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com este id não encontrado"));

        if(!Boolean.TRUE.equals(usuario.getAtivo()))
        {
            throw new RecursoNaoEncontradoException("Usuário com id " + id + " foi desativado.");
        }

        if(usuario.getPermissaoAlerta().equals(request.permissaoAlerta()))
        {
            throw new AlteracaoInvalidaException("A nova permissão é igual a anterior.");
        }

        usuario.setPermissaoAlerta(request.permissaoAlerta());
    }

    @Transactional
    public void atualizarTelefone(AlterarTelefoneUsuarioRequest request, Long id)
    {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com este id não encontrado"));

        if(!Boolean.TRUE.equals(usuario.getAtivo()))
        {
            throw new RecursoNaoEncontradoException("Usuário com id " + id + " foi desativado.");
        }

        if(usuario.getTelefone().equals(request.telefone()))
        {
            throw new AlteracaoInvalidaException("O novo telefone inserido é o mesmo do antigo");
        }

        if(repository.existsByTelefone(request.telefone()))
        {
            throw new AlteracaoInvalidaException("Este número pertence a outro usuário");
        }

        usuario.setTelefone(request.telefone());
    }

    @Transactional
    public void atualizarSenha(AlterarSenhaUsuarioRequest request, Long id)
    {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com este id não encontrado"));

        if(!Boolean.TRUE.equals(usuario.getAtivo()))
        {
            throw new RecursoNaoEncontradoException("Usuário com id " + id + " foi desativado.");
        }

        if(!passwordEncoder.matches(request.senhaAtual(), usuario.getSenhaHash()))
        {
            throw new AlteracaoInvalidaException("A senha atual fornecida é incorreta");
        }

        if(passwordEncoder.matches(request.novaSenha(), usuario.getSenhaHash()))
        {
            throw new AlteracaoInvalidaException("A senha nova é igual a antiga");
        }

        if(!request.novaSenha().equals(request.confirmacaoNovaSenha()))
        {
            throw new AlteracaoInvalidaException("A senha de confirmação é diferente da nova senha");
        }

        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
    }



    public void verificarDuplicidade(String email, String telefone)
    {
        if (repository.existsByEmailIgnoreCase(email))
        {
            throw new RecursoExistenteException("Existe uma conta com este e-mail.");
        }
        if(repository.existsByTelefone(telefone))
        {
            throw new RecursoExistenteException("Existe uma conta com este telefone.");
        }
    }



}
