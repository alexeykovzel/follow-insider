package com.alexeykovzel.fi.features;

import com.alexeykovzel.fi.features.company.CompanyRepository;
import com.alexeykovzel.fi.features.insider.InsiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final CompanyRepository companyRepository;
    private final InsiderRepository insiderRepository;

    @PostMapping
    public void sendQuery(@RequestParam("q") String query) {
        System.out.println("Query received: " + query);
    }

    @GetMapping("/hints")
    public Collection<String> getSearchHints() {
        Collection<String> hints = new HashSet<>();
        hints.addAll(companyRepository.findAllNames());
        hints.addAll(insiderRepository.findAllNames());
        return hints;
    }
}
