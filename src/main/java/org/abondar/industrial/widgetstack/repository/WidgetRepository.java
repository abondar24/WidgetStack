package org.abondar.industrial.widgetstack.repository;

import org.abondar.industrial.widgetstack.model.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetRepository extends JpaRepository<Widget, String> {


}
