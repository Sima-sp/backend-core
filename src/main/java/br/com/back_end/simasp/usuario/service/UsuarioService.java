package br.com.back_end.simasp.usuario.service;

import br.com.back_end.simasp.exception.EmailJaCadastradoException;
import br.com.back_end.simasp.usuario.dto.CriarUsuarioRequest;
import br.com.back_end.simasp.usuario.dto.UsuarioResponse;
import br.com.back_end.simasp.usuario.entity.Usuario;
import br.com.back_end.simasp.usuario.enums.TipoUsuarioEnum;
import br.com.back_end.simasp.usuario.mapper.UsuarioMapper;
import br.com.back_end.simasp.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse cadastroUsuario(CriarUsuarioRequest request)
    {
        String emailFormatado = request.email().toLowerCase(Locale.ROOT).trim();

        if(repository.existsByEmailIgnoreCase(emailFormatado))
        {
            throw new EmailJaCadastradoException("E-mail já cadastrado");
        }

        Usuario usuario = mapper.paraEntidadeUsuario(request);
        usuario.setTipoUsuario(TipoUsuarioEnum.CIDADAO);
        usuario.setEmail(emailFormatado);
        usuario.setSenhaHash(passwordEncoder.encode(request.senhaHash()));

        return mapper.paraUsuarioResponse(repository.save(usuario));
    }

}
