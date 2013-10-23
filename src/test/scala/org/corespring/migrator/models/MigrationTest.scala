package org.corespring.migrator.models

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import org.corespring.migrator.exceptions.NonContiguousMigrationException
import org.corespring.migrator.helpers.{DbSingleton,DbTest}

class MigrationTest extends Specification {

  sequential

  Version.init(DbSingleton.db)

  def script(name:String) = new Script("dbchanges/" + name + ".js", "alert('" + name + "');")

  def version(s:Seq[Script]) = new Version(dateCreated = new DateTime(), scripts = s, versionId ="v1")

  "Migration" should {

    "correctly remove scripts that have already been applied to the current version" in new DbTest {
      val current = script("1")
      val newScript = script("2")
      val v = version(List(current))
      val newScripts = List(current,newScript)
      val newMigration = Migration(v, newScripts)
      newMigration.scripts === newScripts.tail
    }

    "throw an error if there is a missing script - one" in new DbTest {
      val v = version(List(script("1")))
      Migration(v, List(script("0"), script("1"))) must throwA[NonContiguousMigrationException]
    }

    "throw an error if there is a missing script - two" in new DbTest {
      val v = version(List(script("1"), script("2")))
      Migration(v, List(script("1"), script("3"))) must throwA[NonContiguousMigrationException]
    }

    "this is contiguous" in new DbTest{

      val current = Seq(
        "deployment/migrations/0000_adjust_document_structure.js",
        "deployment/migrations/0001_change_dateModified.js",
        "deployment/migrations/0002_add_new_item_types.js",
        "deployment/migrations/0003_rename_type_hints.js",
        "deployment/migrations/0004_add_inline_choice_template.js",
        "deployment/migrations/0004_rename_typehints_sup_materials.js",
        "deployment/migrations/0005_add_new_templates.js",
        "deployment/migrations/0005_b_add_learnzillion_collection.js",
        "deployment/migrations/0006_add_new_item_types.js",
        "deployment/migrations/0007_fix_new_item_types.js",
        "deployment/migrations/0008_delete_unused_contributors.js",
        "deployment/migrations/0009_add_new_prior_use.js",
        "deployment/migrations/0010_change_prior_use_for_TIMSS_items.js",
        "deployment/migrations/0011_change_items_in_public_collections_to_published.js",
        "deployment/migrations/0012_fix_broken_secrets.js",
        "deployment/migrations/0013_update_necap_links.js",
        "deployment/migrations/0014_change_license_types.js",
        "deployment/migrations/0015_remove_comment_from_templates.js",
        "deployment/migrations/0016_removed_dead_collection_ids_from_orgs.js",
        "deployment/migrations/0017_orgs_array_to_org.js",
        "deployment/migrations/0018_remove_old_versions.js",
        "deployment/migrations/0019_clear_quizzes.js",
        "deployment/migrations/0020_add_drag_and_drop_template.js",
        "deployment/migrations/0022_rename_file_type_hints.js",
        "deployment/migrations/0023_newclassrooms_ignore_whitespace.js",
        "deployment/migrations/0024_rename_license_types.js",
        "deployment/migrations/0025_update_nc_metadata_set.js",
        "deployment/migrations/0026_set_credits_for_nc_mc_sa.js",
        "deployment/migrations/0027_qti_responses_type_hint.js",
        "deployment/migrations/0028_newclassrooms_ignore_whitespace.js"
      )

      val proposed = Seq(
        "deployment/migrations/0000_adjust_document_structure.js",
        "deployment/migrations/0001_change_dateModified.js",
        "deployment/migrations/0002_add_new_item_types.js",
        "deployment/migrations/0003_rename_type_hints.js",
        "deployment/migrations/0004_add_inline_choice_template.js",
        "deployment/migrations/0004_rename_typehints_sup_materials.js",
        "deployment/migrations/0005_add_new_templates.js",
        "deployment/migrations/0005_b_add_learnzillion_collection.js",
        "deployment/migrations/0006_add_new_item_types.js",
        "deployment/migrations/0007_fix_new_item_types.js",
        "deployment/migrations/0008_delete_unused_contributors.js",
        "deployment/migrations/0009_add_new_prior_use.js",
        "deployment/migrations/0010_change_prior_use_for_TIMSS_items.js",
        "deployment/migrations/0011_change_items_in_public_collections_to_published.js",
        "deployment/migrations/0012_fix_broken_secrets.js",
        "deployment/migrations/0013_update_necap_links.js",
        "deployment/migrations/0014_change_license_types.js",
        "deployment/migrations/0015_remove_comment_from_templates.js",
        "deployment/migrations/0016_removed_dead_collection_ids_from_orgs.js",
        "deployment/migrations/0017_orgs_array_to_org.js",
        "deployment/migrations/0018_remove_old_versions.js",
        "deployment/migrations/0019_clear_quizzes.js",
        "deployment/migrations/0020_add_drag_and_drop_template.js",
        "deployment/migrations/0022_rename_file_type_hints.js",
        "deployment/migrations/0023_newclassrooms_ignore_whitespace.js",
        "deployment/migrations/0024_rename_license_types.js",
        "deployment/migrations/0025_update_nc_metadata_set.js",
        "deployment/migrations/0026_set_credits_for_nc_mc_sa.js",
        "deployment/migrations/0027_qti_responses_type_hint.js",
        "deployment/migrations/0028_newclassrooms_ignore_whitespace.js",
        "deployment/migrations/0029_newclassrooms_shuffle_mc.js",
        "deployment/migrations/0030_newclassrooms_update_copyright.js",
        "deployment/migrations/0031_newclassrooms_item_feedback.js"
      )

      val v = version(current.map(Script(_, "")))

      val m = Migration(v, proposed.map(Script(_, "")))

      m.scripts.length === 3

    }



  }

}

