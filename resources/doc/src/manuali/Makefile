# Minimal makefile for Sphinx documentation
#

# You can set these variables from the command line.
SPHINXOPTS    =
SPHINXBUILD   = sphinx-build
SOURCEDIR     = .
BUILDDIR      = _build

html: 
	@echo "Producing html files ..."
	@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)
	@echo "Producing html files ok"

clean: 
	rm -rf _build
	rm -rf */_build

pdf: pdf-manuale-installazione pdf-manuale-gestione pdf-manuale-monitoraggio pdf-scenari-applicativi pdf-howto pdf-release-process

pdf-manuale-installazione:
	@echo "Producing pdf Manuale Installazione ..."
	cd installazione; rm -rf pdf; ${SPHINXBUILD} -v -b latex -c .. -Dproject="Guida di Installazione" . "$(BUILDDIR)/pdf"; cd "$(BUILDDIR)/pdf"; make clean all-pdf
	@echo "Producing pdf Manuale Installazione ok"

pdf-manuale-gestione:
	@echo "Producing pdf Manuale Gestione ..."
	cd console; rm -rf pdf; ${SPHINXBUILD} -v -b latex -c .. -Dproject="Guida alla Console di Gestione" . "$(BUILDDIR)/pdf"; cd "$(BUILDDIR)/pdf"; make clean all-pdf
	@echo "Producing pdf Manuale Gestione ok"

pdf-manuale-monitoraggio:
	@echo "Producing pdf Manuale Gestione ..."
	cd monitoraggio; rm -rf pdf; ${SPHINXBUILD} -v -b latex -c .. -Dproject="Guida alla Console di Monitoraggio" . "$(BUILDDIR)/pdf"; cd "$(BUILDDIR)/pdf"; make clean all-pdf
	@echo "Producing pdf Manuale Gestione ok"

pdf-scenari-applicativi:
	@echo "Producing pdf Scenari Applicativi ..."
	cd scenari; rm -rf pdf; ${SPHINXBUILD} -v -b latex -c .. -Dproject="Scenari Applicativi" . "$(BUILDDIR)/pdf"; cd "$(BUILDDIR)/pdf"; make clean all-pdf
	@echo "Producing pdf Scenari Applicativi ok"

pdf-howto:
	@echo "Producing pdf How to ..."
	cd howto; rm -rf pdf; ${SPHINXBUILD} -v -b latex -c .. -Dproject="How-to" . "$(BUILDDIR)/pdf"; cd "$(BUILDDIR)/pdf"; make clean all-pdf
	@echo "Producing pdf How to ok"

pdf-validation-process:
	@echo "Producing pdf Processo di Validazione to ..."
	cd validation-process; rm -rf pdf; ${SPHINXBUILD} -v -b latex -c .. -Dproject="Processo di Validazione" . "$(BUILDDIR)/pdf"; cd "$(BUILDDIR)/pdf"; make clean all-pdf
	@echo "Producing pdf Processo di Validazione to ok"

vulnerability-management:
	@echo "Producing pdf Gestione delle Vulnerabilità to ..."
	cd vulnerability-management; rm -rf pdf; ${SPHINXBUILD} -v -b latex -c .. -Dproject="Gestione delle Vulnerabilità" . "$(BUILDDIR)/pdf"; cd "$(BUILDDIR)/pdf"; make clean all-pdf
	@echo "Producing pdf Gestione delle Vulnerabilità to ok"

post:
	sh ./postprocess.sh

# Put it first so that "make" without argument is like "make help".
#help:
#	@$(SPHINXBUILD) -M help "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

#.PHONY: help Makefile

# Catch-all target: route all unknown targets to Sphinx using the new
# "make mode" option.  $(O) is meant as a shortcut for $(SPHINXOPTS).
#%: Makefile
#	@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)
